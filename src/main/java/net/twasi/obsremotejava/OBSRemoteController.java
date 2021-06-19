package net.twasi.obsremotejava;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.twasi.obsremotejava.listener.lifecycle.ReasonThrowable;
import net.twasi.obsremotejava.listener.lifecycle.controller.ControllerLifecycleListener;
import net.twasi.obsremotejava.listener.lifecycle.controller.LoggingControllerLifecycleListener;
import net.twasi.obsremotejava.message.request.Request;
import net.twasi.obsremotejava.message.request.RequestBatch;
import net.twasi.obsremotejava.message.request.config.*;
import net.twasi.obsremotejava.message.request.general.*;
import net.twasi.obsremotejava.message.request.inputs.*;
import net.twasi.obsremotejava.message.request.scenes.*;
import net.twasi.obsremotejava.message.request.sources.GetSourceActiveRequest;
import net.twasi.obsremotejava.message.request.sources.GetSourceScreenshotRequest;
import net.twasi.obsremotejava.message.request.sources.SaveSourceScreenshotRequest;
import net.twasi.obsremotejava.message.response.RequestBatchResponse;
import net.twasi.obsremotejava.message.response.RequestResponse;
import net.twasi.obsremotejava.message.response.config.*;
import net.twasi.obsremotejava.message.response.general.*;
import net.twasi.obsremotejava.message.response.inputs.*;
import net.twasi.obsremotejava.message.response.scenes.*;
import net.twasi.obsremotejava.message.response.sources.GetSourceActiveResponse;
import net.twasi.obsremotejava.message.response.sources.GetSourceScreenshotResponse;
import net.twasi.obsremotejava.message.response.sources.SaveSourceScreenshotResponse;
import net.twasi.obsremotejava.model.KeyModifiers;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.ConnectException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class OBSRemoteController {

    private String address;
    private final OBSCommunicator communicator;
    private final WebSocketClient webSocketClient;

    private final ControllerLifecycleListener controllerLifecycleListener;

    private boolean failed;

    /**
     * All-Args constructor, used by the builder or directly
     * @param webSocketClient WebSocketClient instance
     * @param communicator Instance of ObsCommunicator (annotated websocket listener)
     * @param controllerLifecycleListener {@link ControllerLifecycleListener}
     * @param host OBS Host
     * @param port OBS port
     * @param autoConnect If true, will connect after this class is instantiated
     */
    public OBSRemoteController(
      WebSocketClient webSocketClient,
      OBSCommunicator communicator,
      ControllerLifecycleListener controllerLifecycleListener,
      String host,
      int port,
      boolean autoConnect) {
        this.webSocketClient = webSocketClient;
        this.communicator = communicator;
        this.controllerLifecycleListener = controllerLifecycleListener;
        this.address = "ws://" + host + ":" + port;
        if (autoConnect) {
            connect();
        }
    }

    public static ObsRemoteControllerBuilder builder() {
        return new ObsRemoteControllerBuilder();
    }

    public void connect() {

        // Try to start the websocket client, this generally shouldn't fail
        try {
            this.webSocketClient.start();
        }
        catch (Exception e) {
            this.controllerLifecycleListener.onError(
              this,
              new ReasonThrowable("Failed to start WebSocketClient", e)
            );
            return;
        }

        // Try to connect over the network with OBS
        try {
            URI uri = new URI(this.address);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            Future<Session> connection = this.webSocketClient.connect(
              this.communicator, uri, request
            );
            log.info(String.format("Connecting to: %s", uri));

            // Block on the connection succeeding
            try {
                connection.get();
                this.failed = false;
                // technically this isn't ready until Identified...consider improving
                // by registering to callback
                this.controllerLifecycleListener.onReady(this);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof ConnectException) {
                    this.failed = true;
                    this.controllerLifecycleListener.onError(this, new ReasonThrowable(
                      "Failed to connect to OBS! Is it running and is the websocket plugin installed?", e
                    ));
                }
                else {
                    throw e;
                }
            }
        }
        catch (Throwable t) {
            this.controllerLifecycleListener.onError(this,
                new ReasonThrowable("Failed to setup connection with OBS", t)
            );
        }
    }

    public void disconnect() {
        // trigger the latch
        try {
            log.debug("Closing connection.");
            this.communicator.awaitClose(1, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            this.controllerLifecycleListener.onError(this,
              new ReasonThrowable("Error during closing websocket connection", e)
            );
        }

        // stop the client if it isn't already stopped or stopping
        if (!this.webSocketClient.isStopped() && !this.webSocketClient.isStopping()) {
            try {
                log.debug("Stopping client.");
                this.webSocketClient.stop();
                // this technically should be registered to a communicator onClose listener
                this.controllerLifecycleListener.onDisconnect(this);
            }
            catch (Exception e) {
                this.controllerLifecycleListener.onError(this,
                  new ReasonThrowable("Error during stopping websocket client", e)
                );
            }
        }
    }

    public boolean isFailed() {
        return this.failed;
    }

    public void await() throws InterruptedException {
        this.communicator.await();
    }

    /**
     * Send a Request
     *
     * @param request R
     * @param callback Consumer&lt;RR&gt;
     * @param <R> extends {@link Request}
     * @param <RR> extends {@link RequestResponse}
     */
    public <R extends Request, RR extends RequestResponse> void sendRequest(R request, Consumer<RR> callback) {
        this.communicator.sendRequest(request, callback);
    }

    /**
     * Send a RequestBatch
     *
     * @param requestBatch {@link RequestBatch}
     * @param callback Consumer&lt;{@link RequestBatchResponse}&gt;
     */
    public void sendRequestBatch(RequestBatch requestBatch, Consumer<RequestBatchResponse> callback) {
        this.communicator.sendRequestBatch(requestBatch, callback);
    }

    public void getVersion(Consumer<GetVersionResponse> callback) {
        this.sendRequest(new GetVersionRequest(), callback);
    }

    public void getStudioModeEnabled(Consumer<GetStudioModeEnabledResponse> callback) {
        this.sendRequest(new GetStudioModeEnabledRequest(), callback);
    }

    public void setStudioModeEnabled(boolean enabled, Consumer<SetStudioModeEnabledResponse> callback) {
        this.sendRequest(new SetStudioModeEnabledRequest(enabled), callback);
    }

    public void broadcastCustomEvent(JsonObject customEventData, Consumer<BroadcastCustomEventResponse> callback) {
        this.sendRequest(new BroadcastCustomEventRequest(customEventData), callback);
    }

    public void sleep(Long millis, Consumer<BroadcastCustomEventResponse> callback) {
        this.sendRequest(new SleepRequest(millis), callback);
    }

    public void getSceneList(Consumer<GetSceneListResponse> callback) {
        this.sendRequest(new GetSceneListRequest(), callback);
    }

    public void getGetHotkeyList(Consumer<GetHotkeyListResponse> callback) {
        this.sendRequest(new GetHotkeyListRequest(), callback);
    }

    public void triggerHotkeyByName(String hotkeyName, Consumer<TriggerHotkeyByNameResponse> callback) {
        this.sendRequest(new TriggerHotkeyByNameRequest(hotkeyName), callback);
    }

    public void triggerHotkeyByKeySequence(String keyId, List<KeyModifiers.KeyModifierType> keyModifiers, Consumer<TriggerHotkeyByKeySequenceResponse> callback) {
        this.sendRequest(new TriggerHotkeyByKeySequenceRequest(keyId, keyModifiers), callback);
    }

    public void getSceneCollectionList(Consumer<GetSceneCollectionListResponse> callback) {
        this.sendRequest(new GetSceneCollectionListRequest(), callback);
    }

    public void setCurrentSceneCollection(String sceneCollectionName, Consumer<SetCurrentSceneCollectionResponse> callback) {
        this.sendRequest(new SetCurrentSceneCollectionRequest(sceneCollectionName), callback);
    }

    public void createSceneCollectionRequest(String sceneCollectionName, Consumer<CreateSceneCollectionResponse> callback) {
        this.sendRequest(new CreateSceneCollectionRequest(sceneCollectionName), callback);
    }

    public void deleteSceneCollectionRequest(String sceneCollectionName, Consumer<DeleteSceneCollectionResponse> callback) {
        this.sendRequest(new DeleteSceneCollectionRequest(sceneCollectionName), callback);
    }

    public void getCurrentProgramSceneRequest(Consumer<GetCurrentProgramSceneResponse> callback) {
        this.sendRequest(new GetCurrentProgramSceneRequest(), callback);
    }

    public void setCurrentProgramSceneRequest(String sceneName, Consumer<SetCurrentProgramSceneResponse> callback) {
        this.sendRequest(new SetCurrentProgramSceneRequest(sceneName), callback);
    }

    public void getCurrentPreviewSceneRequest(Consumer<GetCurrentProgramSceneResponse> callback) {
        this.sendRequest(new GetCurrentProgramSceneRequest(), callback);
    }

    public void setCurrentPreviewSceneRequest(String sceneName, Consumer<SetCurrentPreviewSceneResponse> callback) {
        this.sendRequest(new SetCurrentPreviewSceneRequest(sceneName), callback);
    }

    public void createSceneRequest(String sceneName, Consumer<CreateSceneResponse> callback) {
        this.sendRequest(new CreateSceneRequest(sceneName), callback);
    }

    public void getProfileList(Consumer<GetProfileListResponse> callback) {
        this.sendRequest(new GetProfileListRequest(), callback);
    }

    public void getProfileParameterRequest(String parameterCategory, String parameterName, Consumer<GetProfileParameterResponse> callback) {
        this.sendRequest(new GetProfileParameterRequest(parameterCategory, parameterName), callback);
    }

    public void setProfileParameterRequest(String parameterCategory, String parameterName, String parameterValue, Consumer<SetProfileParameterResponse> callback) {
        this.sendRequest(new SetProfileParameterRequest(parameterCategory, parameterName, parameterValue), callback);
    }

    public void setProfileParameterRequest(String parameterCategory, String parameterName, Consumer<SetProfileParameterResponse> callback) {
        this.sendRequest(new SetProfileParameterRequest(parameterCategory, parameterName), callback);
    }

    public void removeSceneRequest(String sceneName, Consumer<RemoveSceneResponse> callback) {
        this.sendRequest(new RemoveSceneRequest(sceneName), callback);
    }

    public void setSceneName(String sceneName, String newSceneName, Consumer<SetSceneNameResponse> callback) {
        this.sendRequest(new SetSceneNameRequest(sceneName, newSceneName), callback);
    }

    public void getSourceActiveRequest(String sourceName, Consumer<GetSourceActiveResponse> callback) {
        this.sendRequest(new GetSourceActiveRequest(sourceName), callback);
    }

    public void getInputListRequest(Consumer<GetInputListResponse> callback) {
        this.sendRequest(new GetInputListRequest(), callback);
    }

    public void getInputListRequest(String inputKind, Consumer<GetInputListResponse> callback) {
        this.sendRequest(new GetInputListRequest(inputKind), callback);
    }

    public void getInputDefaultSettingsRequest(String inputKind, Consumer<GetInputDefaultSettingsResponse> callback) {
        this.sendRequest(new GetInputDefaultSettingsRequest(inputKind), callback);
    }

    public void getInputKindListRequest(Consumer<GetInputListResponse> callback) {
        this.sendRequest(new GetInputKindListRequest(), callback);
    }

    public void getInputKindListRequest(boolean unversioned, Consumer<GetInputListResponse> callback) {
        this.sendRequest(new GetInputKindListRequest(unversioned), callback);
    }

    public void getInputSettingsRequest(String inputName, Consumer<GetInputSettingsResponse> callback) {
        this.sendRequest(new GetInputSettingsRequest(inputName), callback);
    }

    public void setInputSettingsRequest(String inputName, JsonObject inputSettings, boolean overlay, Consumer<SetInputSettingsResponse> callback) {
        this.sendRequest(new SetInputSettingsRequest(inputName, inputSettings, overlay), callback);
    }

    public void setInputSettingsRequest(String inputName, JsonObject inputSettings, Consumer<SetInputSettingsResponse> callback) {
        this.sendRequest(new SetInputSettingsRequest(inputName, inputSettings), callback);
    }

    public void getInputMuteRequest(String inputName, Consumer<GetInputMuteResponse> callback) {
        this.sendRequest(new GetInputMuteRequest(inputName), callback);
    }

    public void setInputMuteRequest(String inputName, boolean inputMuted, Consumer<SetInputMuteResponse> callback) {
        this.sendRequest(new SetInputMuteRequest(inputName, inputMuted), callback);
    }

    public void toggleInputMuteRequest(String inputName, Consumer<ToggleInputMuteResponse> callback) {
        this.sendRequest(new ToggleInputMuteRequest(inputName), callback);
    }

    public void getInputVolumeRequest(String inputName, Consumer<GetInputVolumeResponse> callback) {
        this.sendRequest(new GetInputVolumeRequest(inputName), callback);
    }

    public void getSourceScreenshotRequest(String sourceName, String imageFormat, Integer imageWidth, Integer imageHeight, Integer imageCompressionQuality, Consumer<GetSourceScreenshotResponse> callback) {
        this.sendRequest(new GetSourceScreenshotRequest(sourceName, imageFormat, imageWidth, imageHeight, imageCompressionQuality), callback);
    }

    public void saveSourceScreenshotRequest(String sourceName, String imageFilePath, String imageFormat, Integer imageWidth, Integer imageHeight, Integer imageCompressionQuality, Consumer<SaveSourceScreenshotResponse> callback) {
        this.sendRequest(new SaveSourceScreenshotRequest(sourceName, imageFilePath, imageFormat, imageWidth, imageHeight, imageCompressionQuality), callback);
    }
}
