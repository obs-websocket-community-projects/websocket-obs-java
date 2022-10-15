package io.obswebsocket.community.client.message.event.scenes;

import io.obswebsocket.community.client.message.event.AbstractEventSerializationTest;
import org.junit.jupiter.api.Test;

class ConfigEventsSerializationTest extends AbstractEventSerializationTest {

  private static final String TYPE = "scenes";

  @Test
  void currentSceneCollectionChangingEvent() {
    assertEventType(TYPE, new SceneCreatedEvent(
        SceneCreatedEvent.SpecificData.builder()
            .sceneName("SomeName")
            .isGroup(true)
            .build()));
  }
}
