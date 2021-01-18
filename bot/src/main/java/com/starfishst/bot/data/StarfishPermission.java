package com.starfishst.bot.data;

import com.starfishst.api.permissions.Permission;
import lombok.Getter;
import lombok.NonNull;

/** An implementation for {@link Permission} */
public class StarfishPermission implements Permission {

  @NonNull @Getter private final String node;
  @Getter private final boolean enabled;

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
}
