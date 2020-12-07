package com.starfishst.api.events.role;

import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.events.StarfishEvent;
import lombok.NonNull;

/** An event that has a bot role involved on it */
public class BotRoleEvent implements StarfishEvent {

  /** The role involved in the event */
  @NonNull private final BotRole role;

  /**
   * Create the event
   *
   * @param role the role involved in the event
   */
  public BotRoleEvent(@NonNull BotRole role) {
    this.role = role;
  }

  /**
   * Get the role involved in the event
   *
   * @return the role involved in the event
   */
  @NonNull
  public BotRole getRole() {
    return role;
  }
}
