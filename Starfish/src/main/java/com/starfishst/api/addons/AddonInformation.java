package com.starfishst.api.addons;

import lombok.NonNull;

/** This class represents the information that the bot needs to enable an Addon */
public interface AddonInformation {

  /**
   * Get the name of the addon
   *
   * @return the name of the addon
   */
  @NonNull
  String getName();

  /**
   * Get the version of the addon
   *
   * @return the version of the addon
   */
  @NonNull
  String getVersion();

  /**
   * Get the description of the addon
   *
   * @return the description of the addon
   */
  @NonNull
  String getDescription();

  /**
   * Get the main class of the addon
   *
   * @return the main class of the addon
   */
  @NonNull
  String getMain();
}
