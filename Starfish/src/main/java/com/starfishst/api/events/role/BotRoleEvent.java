package com.starfishst.api.events.role;

import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.events.StarfishEvent;
import org.jetbrains.annotations.NotNull;

/** An event that has a bot role involved on it */
public class BotRoleEvent implements StarfishEvent {

  /** The role involved in the event */
  @NotNull private final BotRole role;

  /**
   * Create the event
   *
   * @param role the role involved in the event
   */
  public BotRoleEvent(@NotNull BotRole role) {
    this.role = role;
  }

  /**
   * Get the role involved in the event
   *
   * @return the role involved in the event
   */
  @NotNull
  public BotRole getRole() {
    return role;
  }
}
