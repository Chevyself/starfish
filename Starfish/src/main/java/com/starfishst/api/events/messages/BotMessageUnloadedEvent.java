package com.starfishst.api.events.messages;

import com.starfishst.api.data.messages.BotResponsiveMessage;
import lombok.NonNull;

/** Called when a bot message is unloaded */
public class BotMessageUnloadedEvent extends BotMessageEvent {
  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public BotMessageUnloadedEvent(@NonNull BotResponsiveMessage message) {
    super(message);
  }
}
