package com.starfishst.ethot.config;

import com.starfishst.core.utils.time.Time;
import org.jetbrains.annotations.NotNull;

public class AutoSaveConfiguration {

  private final boolean enable;
  @NotNull private final Time toSave;

  public AutoSaveConfiguration(boolean enable, @NotNull Time toSave) {
    this.enable = enable;
    this.toSave = toSave;
  }

  public boolean isEnable() {
    return enable;
  }

  @NotNull
  public Time getToSave() {
    return toSave;
  }
}
