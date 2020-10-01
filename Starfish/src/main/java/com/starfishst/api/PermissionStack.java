package com.starfishst.api;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * A permission stack contains permissions with certain context to use its permissions in different
 * situations
 */
public interface PermissionStack {

  /**
   * Check whether this permission stack has a permission
   *
   * @param node the node of the permission
   * @return true if this has the permission and is enabled
   */
  default boolean hasPermission(@NotNull String node) {
    for (Permission permission : this.getPermissions()) {
      if (permission.getNode().equalsIgnoreCase(node) && permission.isEnabled()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether this stack has a permission and if it is disabled
   *
   * @param node the node of the permission
   * @return true if the stack has the permission
   */
  default boolean containsPermission(@NotNull String node) {
    for (Permission permission : this.getPermissions()) {
      if (permission.getNode().equalsIgnoreCase(node)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the context of the permission stack
   *
   * @return the context of the permission stack
   */
  @NotNull
  String getContext();

  /**
   * Get the permissions that the stack has
   *
   * @return the permission that the stack has
   */
  @NotNull
  Collection<Permission> getPermissions();
}
