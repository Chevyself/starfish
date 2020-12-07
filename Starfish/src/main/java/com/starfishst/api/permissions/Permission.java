package com.starfishst.api.permissions;

import lombok.NonNull;

/** This object represents a permission. A permission contains a node and whether it is enabled */
public interface Permission {

  /**
   * Get the node of the permission. This node is the representation of the permission as a string
   *
   * @return the node if the permission
   */
  @NonNull
  String getNode();

  /**
   * Get if the permission is enabled. If the permission is enabled it means that the member is
   * allowed to use it
   *
   * @return whether the permission is enabled
   */
  boolean isEnabled();

  /**
   * Get node with the {@link #isEnabled()} appended. This will get '-' + the node if {@link
   * #isEnabled()} happens to be false
   *
   * @return the node with the string appended
   */
  @NonNull
  default String getNodeAppended() {
    return this.isEnabled() ? this.getNode() : "-" + this.getNode();
  }
}
