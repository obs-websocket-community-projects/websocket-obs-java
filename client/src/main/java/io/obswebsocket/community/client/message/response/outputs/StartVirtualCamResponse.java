package io.obswebsocket.community.client.message.response.outputs;

import io.obswebsocket.community.client.message.response.RequestResponse;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class StartVirtualCamResponse extends RequestResponse<Void> {

  public StartVirtualCamResponse() {
    super();
  }
}
