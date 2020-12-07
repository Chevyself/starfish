package com.starfishst.api.addons;

import lombok.NonNull;

/** This is the class that every addon made in Java can extend */
public class JavaAddon implements Addon {

  @NonNull private final JavaAddonInformation information;

  public JavaAddon(@NonNull JavaAddonInformation information) {
    this.information = information;
  }

  public void onEnable() throws Throwable {}

  public void onDisable() throws Throwable {}

  @NonNull
  @Override
  public JavaAddonInformation getInformation() {
    return information;
  }
}
