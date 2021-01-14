package com.starfishst.api.permissions;

import java.util.List;
import java.util.Set;
import lombok.NonNull;
import me.googas.commons.Lots;

/** This is an entity which may posses node permissions */
public interface Permissible {

  /**
   * Checks whether the entity posses the permission and it is enabled
   *
   * @param node the node of the permission to get
   * @param context the context to get the stack
   * @return true if the entity posses the permission and it is enabled
   */
  default boolean hasPermission(@NonNull String node, @NonNull String context) {
    PermissionStack stack = this.getPermissions(context);
    return stack != null && stack.hasPermission(node) || stack != null && stack.hasPermission("*");
  }

  /**
   * Checks whether the entity contains the permission
   *
   * @param node the node of the permission to check
   * @param context the context to get the stack
   * @return true if the entity posses the permission
   */
  default boolean containsPermission(@NonNull String node, @NonNull String context) {
    PermissionStack stack = this.getPermissions(context);
    return stack != null && stack.containsPermission(node);
  }

  /**
   * Get a permission stack from this permissible entity using its context
   *
   * @param context the context that differentiates the stack
   * @return the stack of permissions if found else null
   */
  default PermissionStack getPermissions(@NonNull String context) {
    for (PermissionStack permission : this.getPermissions()) {
      if (permission.getContext().equalsIgnoreCase(context)) {
        return permission;
      }
    }
    return null;
  }

  /**
   * Get the set of permission stack. This are all the permissions that this entity posses
   *
   * @return the set of permissions of the entity
   */
  @NonNull
  Set<PermissionStack> getPermissions();
}
