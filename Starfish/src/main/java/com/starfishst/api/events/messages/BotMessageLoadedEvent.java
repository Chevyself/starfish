package com.starfishst.api.events.messages;

import com.starfishst.api.data.messages.BotResponsiveMessage;
import org.jetbrains.annotations.NotNull;

/** Called when a bot responsive message is loaded */
public class BotMessageLoadedEvent extends BotMessageEvent {
  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public BotMessageLoadedEvent(@NotNull BotResponsiveMessage message) {
    super(message);
  }
}
