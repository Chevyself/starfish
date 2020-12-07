package com.starfishst.bot.data;

import com.starfishst.api.permissions.Permission;
import lombok.NonNull;

/** An implementation for {@link Permission} */
public class StarfishPermission implements Permission {

  /** The node of the permission */
  @NonNull private final String node;

  /** Whether the permission is enabled */
  private final boolean enabled;

  /**
   * Create the permission
   *
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   */
  public StarfishPermission(@NonNull String node, boolean enabled) {
    this.node = node;
    this.enabled = enabled;
  }

  @Override
  public @NonNull String getNode() {
    return this.node;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
