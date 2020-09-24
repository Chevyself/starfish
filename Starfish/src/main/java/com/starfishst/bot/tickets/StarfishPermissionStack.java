package com.starfishst.bot.tickets;

import com.starfishst.api.Permission;
import com.starfishst.api.PermissionStack;
import java.util.Collection;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link PermissionStack} */
public class StarfishPermissionStack implements PermissionStack {

  /** The context of the stack */
  @NotNull private final String context;

  /** The permissions in the stack */
  @NotNull private final Set<Permission> permissions;

  /**
   * Create the permission stack
   *
   * @param context the context of the stack
   * @param permissions the permission that the stack has
   */
  public StarfishPermissionStack(@NotNull String context, @NotNull Set<Permission> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  @Override
  public @NotNull String getContext() {
    return this.context;
  }

  @Override
  public @NotNull Collection<Permission> getPermissions() {
    return this.permissions;
  }
}
