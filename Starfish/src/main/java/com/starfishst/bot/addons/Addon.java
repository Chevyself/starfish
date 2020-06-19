package com.starfishst.bot.addons;

import com.starfishst.bot.util.Console;
import org.jetbrains.annotations.Nullable;

/** Every addon should extend this in the main class */
public class Addon {

  /** The addon information */
  @Nullable private AddonInformation information;

  /** Executed when the addon is loaded */
  public void onEnable() {
    Console.info(information.getName() + " has been loaded!");
  }

  /** Executed when the addon is unloaded */
  public void onDisable() {
    Console.info(information.getName() + " has been unloaded!");
  }

  /**
   * Set the addon information
   *
   * @param information the new addon information
   */
  void setInformation(@Nullable AddonInformation information) {
    this.information = information;
  }

  /**
   * Get the addon information
   *
   * @return the addon information
   */
  @Nullable
  public AddonInformation getInformation() {
    return information;
  }
}
