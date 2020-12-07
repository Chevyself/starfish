package com.starfishst.api.addons;

import lombok.NonNull;

/** Each addon has important information which is required to initialize it */
public class JavaAddonInformation implements AddonInformation {

  @NonNull private final String name;

  @NonNull private final String version;

  @NonNull private final String description;

  @NonNull private final String main;

  public JavaAddonInformation(
      @NonNull String name,
      @NonNull String version,
      @NonNull String description,
      @NonNull String main) {
    this.name = name;
    this.version = version;
    this.description = description;
    this.main = main;
  }

  @Override
  @NonNull
  public String getName() {
    return name;
  }

  @Override
  @NonNull
  public String getVersion() {
    return version;
  }

  @Override
  @NonNull
  public String getDescription() {
    return description;
  }

  @Override
  @NonNull
  public String getMain() {
    return main;
  }
}
