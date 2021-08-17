package com.starfishst.api.events.role;

import com.starfishst.api.role.BotRole;
import lombok.NonNull;

/** Called when a role is loaded */
@Deprecated
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
