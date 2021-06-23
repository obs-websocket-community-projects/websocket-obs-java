package net.twasi.obsremotejava.message.request;

import java.util.UUID;

import lombok.Getter;
import lombok.ToString;
import net.twasi.obsremotejava.message.Message;
import net.twasi.obsremotejava.message.request.config.*;
import net.twasi.obsremotejava.message.request.filters.*;
import net.twasi.obsremotejava.message.request.general.*;
import net.twasi.obsremotejava.message.request.inputs.*;
import net.twasi.obsremotejava.message.request.scenes.*;
import net.twasi.obsremotejava.message.request.sources.GetSourceActiveRequest;
import net.twasi.obsremotejava.message.request.sources.GetSourceScreenshotRequest;
import net.twasi.obsremotejava.message.request.sources.SaveSourceScreenshotRequest;
import net.twasi.obsremotejava.message.request.transitions.*;
import net.twasi.obsremotejava.message.response.RequestResponse;
import net.twasi.obsremotejava.message.response.config.*;
import net.twasi.obsremotejava.message.response.filters.*;
import net.twasi.obsremotejava.message.response.general.*;
import net.twasi.obsremotejava.message.response.inputs.*;
import net.twasi.obsremotejava.message.response.scenes.*;
import net.twasi.obsremotejava.message.response.sources.GetSourceActiveResponse;
import net.twasi.obsremotejava.message.response.sources.GetSourceScreenshotResponse;
import net.twasi.obsremotejava.message.response.sources.SaveSourceScreenshotResponse;
import net.twasi.obsremotejava.message.response.transitions.*;

@Getter
@ToString(callSuper = true)
public abstract class Request extends Message {
    protected Type requestType;
    protected String requestId;

    protected Request(Type type) {
        super(Message.Type.Request);

        this.requestType = type;
        this.requestId = UUID.randomUUID().toString();
    }

    @Getter
    public enum Type {
        // General
        GetVersion(GetVersionRequest.class, GetVersionResponse.class),
        BroadcastCustomEvent(BroadcastCustomEventRequest.class, BroadcastCustomEventResponse.class),
        GetHotkeyList(GetHotkeyListRequest.class, GetHotkeyListResponse.class),
        TriggerHotkeyByName(TriggerHotkeyByNameRequest.class, TriggerHotkeyByNameResponse.class),
        TriggerHotkeyByKeySequence(TriggerHotkeyByKeySequenceRequest.class, TriggerHotkeyByKeySequenceResponse.class),
        GetStudioModeEnabled(GetStudioModeEnabledRequest.class, GetStudioModeEnabledResponse.class),
        SetStudioModeEnabled(SetStudioModeEnabledRequest.class, SetStudioModeEnabledResponse.class),
        Sleep(SleepRequest.class, SleepResponse.class),
        OpenProjector(OpenProjectorRequest.class, OpenProjectorResponse.class),

        // Config
        GetSceneCollectionList(GetSceneCollectionListRequest.class, GetSceneCollectionListResponse.class),
        SetCurrentSceneCollection(SetCurrentSceneCollectionRequest.class, SetCurrentSceneCollectionResponse.class),
        CreateSceneCollection(CreateSceneCollectionRequest.class, CreateSceneCollectionResponse.class),
        RemoveSceneCollection(RemoveSceneCollectionRequest.class, RemoveSceneCollectionResponse.class),
        GetProfileList(GetProfileListRequest.class, GetProfileListResponse.class),
        GetProfileParameter(GetProfileParameterRequest.class, GetProfileParameterResponse.class),
        SetProfileParameter(SetProfileParameterRequest.class, SetProfileParameterResponse.class),
        GetVideoSettings(GetVideoSettingsRequest.class, GetVideoSettingsResponse.class),

        // Scenes
        GetSceneList(GetSceneListRequest.class, GetSceneListResponse.class),
        GetCurrentProgramScene(GetCurrentProgramSceneRequest.class, GetCurrentProgramSceneResponse.class),
        SetCurrentProgramScene(SetCurrentProgramSceneRequest.class, SetCurrentProgramSceneResponse.class),
        GetCurrentPreviewScene(GetCurrentPreviewSceneRequest.class, GetCurrentPreviewSceneResponse.class),
        SetCurrentPreviewScene(SetCurrentPreviewSceneRequest.class, SetCurrentPreviewSceneResponse.class),
        CreateScene(CreateSceneRequest.class, CreateSceneResponse.class),
        SetSceneName(SetSceneNameRequest.class, SetSceneNameResponse.class),
        RemoveScene(RemoveSceneRequest.class, RemoveSceneResponse.class),
        GetSceneTransitionOverride(GetSceneTransitionOverrideRequest.class, GetSceneTransitionOverrideResponse.class),
        SetSceneTransitionOverride(SetSceneTransitionOverrideRequest.class, SetSceneTransitionOverrideResponse.class),
        DeleteSceneTransitionOverride(DeleteSceneTransitionOverrideRequest.class, DeleteSceneTransitionOverrideResponse.class),

        // Sources
        GetSourceActive(GetSourceActiveRequest.class, GetSourceActiveResponse.class),
        GetSourceScreenshot(GetSourceScreenshotRequest.class, GetSourceScreenshotResponse.class),
        SaveSourceScreenshot(SaveSourceScreenshotRequest.class, SaveSourceScreenshotResponse.class),

        // Inputs
        GetInputList(GetInputListRequest.class, GetInputListResponse.class),
        GetInputKindList(GetInputKindListRequest.class, GetInputKindListResponse.class),
        GetInputDefaultSettings(GetInputDefaultSettingsRequest.class, GetInputDefaultSettingsResponse.class),
        GetInputSettings(GetInputSettingsRequest.class, GetInputSettingsResponse.class),
        SetInputSettings(SetInputSettingsRequest.class, SetInputSettingsResponse.class),
        GetInputMute(GetInputMuteRequest.class, GetInputMuteResponse.class),
        SetInputMute(SetInputMuteRequest.class, SetInputMuteResponse.class),
        ToggleInputMute(ToggleInputMuteRequest.class, ToggleInputMuteResponse.class),
        GetInputVolume(GetInputVolumeRequest.class, GetInputVolumeResponse.class),
        GetSpecialInputNames(GetSpecialInputNamesRequest.class, GetSpecialInputNamesResponse.class),
        SetInputName(SetInputNameRequest.class, SetInputNameResponse.class),
        SetInputVolume(SetInputVolumeRequest.class, SetInputVolumeResponse.class),
        CreateInput(CreateInputRequest.class, CreateInputResponse.class),
        GetInputTracks(GetInputTracksRequest.class, GetInputTracksResponse.class),
        GetInputMonitorType(GetInputMonitorTypeRequest.class, GetInputMonitorTypeResponse.class),
        SetInputMonitorType(SetInputMonitorTypeRequest.class, SetInputMonitorTypeResponse.class),

        // Transitions
        GetTransitionList(GetTransitionListRequest.class, GetTransitionListResponse.class),
        GetCurrentTransition(GetCurrentTransitionRequest.class, GetCurrentTransitionResponse.class),
        SetCurrentTransition(SetCurrentTransitionRequest.class, SetCurrentTransitionResponse.class),
        SetCurrentTransitionDuration(SetCurrentTransitionDurationRequest.class, SetCurrentTransitionDurationResponse.class),
        GetTransitionSettings(GetTransitionSettingsRequest.class, GetTransitionSettingsResponse.class),
        SetTransitionSettings(SetTransitionSettingsRequest.class, SetTransitionSettingsResponse.class),
        ReleaseTbar(ReleaseTbarRequest.class, ReleaseTbarResponse.class),
        SetTbarPosition(SetTbarPositionRequest.class, SetTbarPositionResponse.class),
        TriggerStudioModeTransition(TriggerStudioModeTransitionRequest.class, TriggerStudioModeTransitionResponse.class),

        // Filters
        GetSourceFilterList(GetSourceFilterListRequest.class, GetSourceFilterListResponse.class),
        GetSourceFilter(GetSourceFilterRequest.class, GetSourceFilterResponse.class),
        SetSourceFilterIndex(SetSourceFilterIndexRequest.class, SetSourceFilterIndexResponse.class),
        SetSourceFilterSettings(SetSourceFilterSettingsRequest.class, SetSourceFilterSettingsResponse.class),
        SetSourceFilterEnabled(SetSourceFilterEnabledRequest.class, SetSourceFilterEnabledResponse.class),
        CreateSourceFilter(CreateSourceFilterRequest.class, CreateSourceFilterResponse.class),
        RemoveSourceFilter(RemoveSourceFilterRequest.class, null),
        ;

        private final Class<? extends Request> requestClass;
        private final Class<? extends RequestResponse> requestResponseClass;

        Type(Class<? extends Request> requestClass, Class<? extends RequestResponse> requestResponseClass) {
            this.requestClass = requestClass;
            this.requestResponseClass = requestResponseClass;
        }
    }
}
