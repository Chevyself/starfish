package com.starfishst.api.events.user;

import com.starfishst.api.events.StarfishEvent;
import com.starfishst.api.user.BotUser;
import lombok.Getter;
import lombok.NonNull;

/** An event that has an user involved */
public class BotUserEvent implements StarfishEvent {

  @NonNull @Getter private final BotUser user;

  /**
   * Create the event
   *
   * @param user the user involved in the event
   */
  public BotUserEvent(@NonNull BotUser user) {
    this.user = user;
  }
}
