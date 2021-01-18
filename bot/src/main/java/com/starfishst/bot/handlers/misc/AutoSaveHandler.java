package com.starfishst.bot.handlers.misc;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import lombok.CustomLog;
import lombok.NonNull;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** A task to auto-save the config */
@CustomLog
public class AutoSaveHandler implements StarfishHandler, Runnable {

  /**
   * Get the time to auto save
   *
   * @return the time to auto save
   */
  @NonNull
  private Time getTime() {
    try {
      return Time.fromString(this.getPreferences().getOr("time", String.class, "2m"));
    } catch (IllegalArgumentException e) {
      Starfish.getFallback()
          .process(
              e,
              "Auto Save: "
                  + this.getPreferences().get("time", String.class)
                  + " is not valid time!");
      return new Time(2, Unit.MINUTES);
    }
  }

  @Override
  public void onEnable() {
    if (this.getPreferences().getOr("enabled", Boolean.class, false)) {
      Starfish.getScheduler().repeat(this.getTime(), this.getTime(), this);
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "auto-save";
  }

  @Override
  public void run() {
    Starfish.save();
    AutoSaveHandler.log.fine("Auto-Save completed");
  }
}
