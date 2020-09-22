package com.starfishst.bot.oldconfig;

import com.starfishst.core.utils.time.Time;
import org.jetbrains.annotations.NotNull;

/** The configuration for auto-saving */
public class AutoSaveConfiguration {

  /** Is auto saving enabled */
  private final boolean enable;
  /** The time to save */
  @NotNull private final Time toSave;

  /**
   * Create an instance
   *
   * @param enable is auto saving enabled?
   * @param toSave the time to auto-save
   */
  public AutoSaveConfiguration(boolean enable, @NotNull Time toSave) {
    this.enable = enable;
    this.toSave = toSave;
  }

  /**
   * Get if auto-saving is enabled
   *
   * @return true if is enabled
   */
  public boolean isEnable() {
    return enable;
  }

  /**
   * Get the time to auto-save
   *
   * @return the time to auto-save
   */
  @NotNull
  public Time getToSave() {
    return toSave;
  }
}
