package com.starfishst.api.addons;

import org.jetbrains.annotations.NotNull;

/** Every addon should extend this in the main class */
public class Addon {

  /** The addon information */
  @NotNull private final AddonInformation information;

  /**
   * Create the addon.
   *
   * @param information The addon information;
   */
  public Addon(@NotNull AddonInformation information) {
    this.information = information;
  }

  /** Executed when the addon is loaded */
  public void onEnable() {}

  /** Executed when the addon is unloaded */
  public void onDisable() {}

  /**
   * Get the addon information
   *
   * @return the addon information
   */
  @NotNull
  public AddonInformation getInformation() {
    return information;
  }
}
