package com.starfishst.api.events.role;

import com.starfishst.api.data.role.BotRole;
import lombok.NonNull;

/** Called when a role is loaded */
public class BotRoleUnloadedEvent extends BotRoleEvent {

  /**
   * Create the event
   *
   * @param role the role involved in the event
   */
  public BotRoleUnloadedEvent(@NonNull BotRole role) {
    super(role);
  }
}
