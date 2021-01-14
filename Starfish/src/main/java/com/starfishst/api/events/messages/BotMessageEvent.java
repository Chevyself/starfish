package com.starfishst.api.events.messages;

import com.starfishst.api.events.StarfishEvent;
import com.starfishst.api.messages.BotResponsiveMessage;
import lombok.Getter;
import lombok.NonNull;

/** An event that has a {@link BotResponsiveMessage} involved */
public class BotMessageEvent implements StarfishEvent {

  @NonNull @Getter private final BotResponsiveMessage message;

  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public BotMessageEvent(@NonNull BotResponsiveMessage message) {
    this.message = message;
  }
}
