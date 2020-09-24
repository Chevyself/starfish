package com.starfishst.bot.data;

import com.starfishst.api.Permission;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link Permission} */
public class StarfishPermission implements Permission {

  /** The node of the permission */
  @NotNull private final String node;

  /** Whether the permission is enabled */
  private boolean enabled;

  /**
   * Create the permission
   *
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   */
  public StarfishPermission(@NotNull String node, boolean enabled) {
    this.node = node;
    this.enabled = enabled;
  }

  @Override
  public @NotNull String getNode() {
    return this.node;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
