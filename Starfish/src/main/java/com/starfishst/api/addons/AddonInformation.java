package com.starfishst.api.addons;

import org.jetbrains.annotations.NotNull;

/** Each addon has important information which is required to initialize it */
public class AddonInformation {

  /** The name of the addon */
  @NotNull private final String name;
  /** The version of the addon */
  @NotNull private final String version;
  /** The description of the addon */
  @NotNull private final String description;
  /** The main class of the addon */
  @NotNull private final String main;

  /**
   * Create the addon information. It should be loaded using gson
   *
   * @param name the name of the addon
   * @param version the version of the addon
   * @param description the description of the addon
   * @param main the path to the main class of the addon
   */
  public AddonInformation(
      @NotNull String name,
      @NotNull String version,
      @NotNull String description,
      @NotNull String main) {
    this.name = name;
    this.version = version;
    this.description = description;
    this.main = main;
  }

  /**
   * Get the name of the addon
   *
   * @return the name of the addon
   */
  @NotNull
  public String getName() {
    return name;
  }

  /**
   * Get the version of the addon
   *
   * @return the version of the addon
   */
  @NotNull
  public String getVersion() {
    return version;
  }

  /**
   * Get the description of the addon
   *
   * @return the description of the addon
   */
  @NotNull
  public String getDescription() {
    return description;
  }

  /**
   * Get the main class of the addon
   *
   * @return the main class of the addon
   */
  @NotNull
  public String getMain() {
    return main;
  }
}
