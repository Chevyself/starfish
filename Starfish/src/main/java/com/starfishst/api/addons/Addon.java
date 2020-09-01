package com.starfishst.api.addons;

import com.starfishst.bot.util.Console;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Every addon should extend this in the main class */
public class Addon {

  /** The addon information */
  @NotNull
  private final AddonInformation information;

  /**
   * Create the addon.
   *
   * @param information The addon information;
   */
  public Addon(@NotNull AddonInformation information) {
    this.information = information;
  }

  /** Executed when the addon is loaded */
  public void onEnable() {
    Console.info(information.getName() + " has been loaded!");
  }

  /** Executed when the addon is unloaded */
  public void onDisable() {
    Console.info(information.getName() + " has been unloaded!");
  }

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
