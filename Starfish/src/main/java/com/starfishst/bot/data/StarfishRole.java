package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.events.role.BotRoleUnloadedEvent;
import com.starfishst.api.permissions.PermissionStack;
import java.util.Set;
import lombok.NonNull;
import me.googas.commons.time.Time;

/** An implementation for {@link BotRole} */
public class StarfishRole implements BotRole {

  /** The id of the role */
  private final long id;
  /** The set of permissions that the role has */
  private final Set<PermissionStack> permissions;

  /**
   * Create the starfish role
   *
   * @param id the unique id of the role given by discord
   * @param permissions the permissions that the role has
   */
  public StarfishRole(long id, Set<PermissionStack> permissions) {
    this.id = id;
    this.permissions = permissions;
  }

  @Override
  public void onRemove() {
    new BotRoleUnloadedEvent(this);
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().toUnloadUser();
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NonNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }
}
