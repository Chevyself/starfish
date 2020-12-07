package com.starfishst.bot.tickets;

import com.starfishst.api.permissions.Permission;
import com.starfishst.api.permissions.PermissionStack;
import java.util.Collection;
import java.util.Set;
import lombok.NonNull;

/** An implementation for {@link PermissionStack} */
public class StarfishPermissionStack implements PermissionStack {

  /** The context of the stack */
  @NonNull private final String context;

  /** The permissions in the stack */
  @NonNull private final Set<Permission> permissions;

  /**
   * Create the permission stack
   *
   * @param context the context of the stack
   * @param permissions the permission that the stack has
   */
  public StarfishPermissionStack(@NonNull String context, @NonNull Set<Permission> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  @Override
  public @NonNull String getContext() {
    return this.context;
  }

  @Override
  public @NonNull Collection<Permission> getPermissions() {
    return this.permissions;
  }
}
