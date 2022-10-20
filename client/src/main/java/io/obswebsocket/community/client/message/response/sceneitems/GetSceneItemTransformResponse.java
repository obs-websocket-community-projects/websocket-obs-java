// @formatter:off
// This class is generated, do not edit!
package io.obswebsocket.community.client.message.response.sceneitems;

import io.obswebsocket.community.client.message.response.RequestResponse;
import io.obswebsocket.community.client.model.SceneItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(
    callSuper = true
)
public class GetSceneItemTransformResponse extends RequestResponse<GetSceneItemTransformResponse.SpecificData> {
  @Getter
  @ToString
  @Builder
  public static class SpecificData {
    /**
     * Object containing scene item transform info
     */
    @NonNull
    private SceneItem.Transform sceneItemTransform;
  }
}
