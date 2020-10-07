package com.starfishst.bot.data;

import com.starfishst.api.PermissionStack;
import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.events.role.BotRoleLoadedEvent;
import com.starfishst.api.events.role.BotRoleUnloadedEvent;
import com.starfishst.bot.Starfish;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link BotRole} */
public class StarfishRole extends Catchable implements BotRole {

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
    super(Starfish.getConfiguration().toUnloadUser());
    this.id = id;
    this.permissions = permissions;
    new BotRoleLoadedEvent(this);
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new BotRoleUnloadedEvent(this);
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }
}
