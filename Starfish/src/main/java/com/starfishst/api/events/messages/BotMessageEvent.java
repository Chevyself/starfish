package com.starfishst.api.events.messages;

import com.starfishst.api.data.messages.BotResponsiveMessage;
import com.starfishst.api.events.StarfishEvent;
import lombok.NonNull;

/** An event that has a {@link com.starfishst.api.data.messages.BotResponsiveMessage} involved */
public class BotMessageEvent implements StarfishEvent {

  /** The message involved in the event */
  @NonNull private final BotResponsiveMessage message;

  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public BotMessageEvent(@NonNull BotResponsiveMessage message) {
    this.message = message;
  }

  /**
   * Get the message involved in the event
   *
   * @return the message involved in the event
   */
  @NonNull
  public BotResponsiveMessage getMessage() {
    return message;
  }
}
