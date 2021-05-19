package net.twasi.obsremotejava.requests.StopMedia;

import net.twasi.obsremotejava.OBSCommunicator;
import net.twasi.obsremotejava.requests.BaseRequest;
import net.twasi.obsremotejava.requests.RequestType;

public class StopMediaRequest extends BaseRequest {
    private String sourceName;

    public StopMediaRequest(OBSCommunicator com, String sourceName) {
        super(RequestType.StopMedia);

        this.sourceName = sourceName;

        com.messageTypes.put(getMessageId(), StopMediaResponse.class);
    }
}
