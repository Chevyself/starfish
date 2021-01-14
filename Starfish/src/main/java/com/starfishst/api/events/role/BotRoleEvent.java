package com.starfishst.api.events.role;

import com.starfishst.api.events.StarfishEvent;
import com.starfishst.api.role.BotRole;
import lombok.Getter;
import lombok.NonNull;

/** An event that has a bot role involved on it */
public class BotRoleEvent implements StarfishEvent {

  @NonNull @Getter private final BotRole role;

  /**
   * Create the event
   *
   * @param role the role involved in the event
   */
  public BotRoleEvent(@NonNull BotRole role) {
    this.role = role;
  }
}
