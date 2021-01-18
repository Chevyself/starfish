package com.starfishst.api.events.user;

import com.starfishst.api.user.BotUser;
import lombok.NonNull;

/** Called when a bot user gets unloaded */
public class BotUserUnloadedEvent extends BotUserEvent {
  /**
   * Create the event
   *
   * @param user the user involved in the event
   */
  public BotUserUnloadedEvent(@NonNull BotUser user) {
    super(user);
  }
}
