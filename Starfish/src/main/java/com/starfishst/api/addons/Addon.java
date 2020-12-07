package com.starfishst.api.addons;

import lombok.NonNull;

/** This object represents an addon created to expand the Starfish bot */
public interface Addon {

  /** Called when the addon is enabled */
  void onEnable() throws Throwable;

  /** Called when the addon is disabled */
  void onDisable() throws Throwable;

  /**
   * Get the addon information
   *
   * @return the addon information
   */
  @NonNull
  JavaAddonInformation getInformation();
}
