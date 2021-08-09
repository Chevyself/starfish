package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.role.BotRoleUnloadedEvent;
import com.starfishst.api.permissions.PermissionStack;
import com.starfishst.api.role.BotRole;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.starbox.time.Time;

/** An implementation for {@link BotRole} */
public class StarfishRole implements BotRole {

  @Getter private final long id;
  @NonNull @Getter private final Set<PermissionStack> permissions;

  /**
   * Create the starfish role
   *
   * @param id the unique id of the role given by discord
   * @param permissions the permissions that the role has
   */
  public StarfishRole(long id, @NonNull Set<PermissionStack> permissions) {
    this.id = id;
    this.permissions = permissions;
  }

  @Override
  public void onRemove() {
    new BotRoleUnloadedEvent(this);
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().getUnloadUsers();
  }

  @Override
  public @NonNull StarfishRole cache() {
    return (StarfishRole) BotRole.super.cache();
  }
}
