package io.obswebsocket.community.client.message.event.ui;

import io.obswebsocket.community.client.message.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Studio mode has been enabled or disabled.
 *
 * This class is generated, do not edit!
 */
@Getter
@ToString(
    callSuper = true
)
public class StudioModeStateChangedEvent extends Event<StudioModeStateChangedEvent.SpecificData> {
  protected StudioModeStateChangedEvent() {
    super(Intent.Ui);
  }

  protected StudioModeStateChangedEvent(StudioModeStateChangedEvent.SpecificData data) {
    super(Intent.Ui, data);
  }

  @Getter
  @ToString
  @Builder
  public static class SpecificData {
    /**
     * True == Enabled, False == Disabled
     */
    @NonNull
    private Boolean studioModeEnabled;
  }
}
