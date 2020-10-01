package com.starfishst.api.events.messages;

import com.starfishst.api.data.messages.BotResponsiveMessage;
import com.starfishst.api.events.StarfishEvent;
import org.jetbrains.annotations.NotNull;

/** An event that has a {@link com.starfishst.api.data.messages.BotResponsiveMessage} involved */
public class BotMessageEvent implements StarfishEvent {

  /** The message involved in the event */
  @NotNull private final BotResponsiveMessage message;

  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public BotMessageEvent(@NotNull BotResponsiveMessage message) {
    this.message = message;
  }

  /**
   * Get the message involved in the event
   *
   * @return the message involved in the event
   */
  @NotNull
  public BotResponsiveMessage getMessage() {
    return message;
  }
}
