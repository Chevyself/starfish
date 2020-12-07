package com.starfishst.api.events.user;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.StarfishEvent;
import lombok.NonNull;

/** An event that has an user involved */
public class BotUserEvent implements StarfishEvent {

  /** The user involved in the event */
  @NonNull private final BotUser user;

  /**
   * Create the event
   *
   * @param user the user involved in the event
   */
  public BotUserEvent(@NonNull BotUser user) {
    this.user = user;
  }

  /**
   * Get the user involved in the event
   *
   * @return the user involved in the event
   */
  @NonNull
  public BotUser getUser() {
    return user;
  }
}
