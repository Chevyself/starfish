package com.starfishst.api.permissions;

import java.util.Optional;
import java.util.Set;
import lombok.NonNull;

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
    Optional<PermissionStack> optionalStack = this.getPermissions(context);
    return optionalStack.isPresent() && optionalStack.get().hasPermission(node) || optionalStack.isPresent() && optionalStack.get().hasPermission("*");
  }

  /**
   * Checks whether the entity contains the permission
   *
   * @param node the node of the permission to check
   * @param context the context to get the stack
   * @return true if the entity posses the permission
   */
  default boolean containsPermission(@NonNull String node, @NonNull String context) {
    Optional<PermissionStack> optionalStack = this.getPermissions(context);
    return optionalStack.isPresent() && optionalStack.get().containsPermission(node);
  }

  /**
   * Get a permission stack from this permissible entity using its context
   *
   * @param context the context that differentiates the stack
   * @return a {@link java.util.Optional} holding the nullable stack
   */
  @NonNull
  default Optional<PermissionStack> getPermissions(@NonNull String context) {
    return this.getPermissions().stream().filter(stack -> stack.getContext().equalsIgnoreCase(context)).findFirst();
  }

  /**
   * Get the set of permission stack. This are all the permissions that this entity posses
   *
   * @return the set of permissions of the entity
   */
  @NonNull
  Set<PermissionStack> getPermissions();
}
