package io.obswebsocket.community.client.message.event.general;

import com.google.gson.JsonObject;
import io.obswebsocket.community.client.message.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * An event has been emitted from a vendor.
 *
 * A vendor is a unique name registered by a third-party plugin or script, which allows for custom requests and events to be added to obs-websocket.
 * If a plugin or script implements vendor requests or events, documentation is expected to be provided with them.
 *
 * This class is generated, do not edit!
 */
@Getter
@ToString(
    callSuper = true
)
public class VendorEventEvent extends Event<VendorEventEvent.SpecificData> {
  protected VendorEventEvent() {
    super(Intent.Vendors);
  }

  protected VendorEventEvent(VendorEventEvent.SpecificData data) {
    super(Intent.Vendors, data);
  }

  @Getter
  @ToString
  @Builder
  public static class SpecificData {
    /**
     * Name of the vendor emitting the event
     */
    @NonNull
    private String vendorName;

    /**
     * Vendor-provided event typedef
     */
    @NonNull
    private String eventType;

    /**
     * Vendor-provided event data. {} if event does not provide any data
     */
    @NonNull
    private JsonObject eventData;
  }
}
