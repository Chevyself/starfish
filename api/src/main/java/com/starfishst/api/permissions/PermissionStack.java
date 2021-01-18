package com.starfishst.api.permissions;

import java.util.Collection;
import lombok.NonNull;

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
  default boolean hasPermission(@NonNull String node) {
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
  default boolean containsPermission(@NonNull String node) {
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
  @NonNull
  String getContext();

  /**
   * Get the permissions that the stack has
   *
   * @return the permission that the stack has
   */
  @NonNull
  Collection<Permission> getPermissions();
}
