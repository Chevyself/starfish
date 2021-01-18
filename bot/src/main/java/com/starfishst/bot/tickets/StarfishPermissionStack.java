package com.starfishst.bot.tickets;

import com.starfishst.api.permissions.Permission;
import com.starfishst.api.permissions.PermissionStack;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;

/** An implementation for {@link PermissionStack} */
public class StarfishPermissionStack implements PermissionStack {

  @NonNull @Getter private final String context;
  @NonNull @Getter private final Set<Permission> permissions;

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
}
