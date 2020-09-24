package com.starfishst.api.events.role;

import com.starfishst.api.data.role.BotRole;
import org.jetbrains.annotations.NotNull;

/** Called when a role is loaded */
public class BotRoleLoadedEvent extends BotRoleEvent {

  /**
   * Create the event
   *
   * @param role the role involved in the event
   */
  public BotRoleLoadedEvent(@NotNull BotRole role) {
    super(role);
  }
}
