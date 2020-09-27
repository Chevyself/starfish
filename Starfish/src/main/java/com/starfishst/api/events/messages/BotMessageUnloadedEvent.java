package com.starfishst.api.events.messages;

import com.starfishst.api.data.messages.BotResponsiveMessage;
import org.jetbrains.annotations.NotNull;

/** Called when a bot message is unloaded */
public class BotMessageUnloadedEvent extends BotMessageEvent {
  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public BotMessageUnloadedEvent(@NotNull BotResponsiveMessage message) {
    super(message);
  }
}
