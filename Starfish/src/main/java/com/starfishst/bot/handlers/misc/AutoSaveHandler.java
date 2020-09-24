package com.starfishst.bot.handlers.misc;

import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.StarfishHandler;
import com.starfishst.core.utils.time.Time;
import java.util.Timer;
import java.util.TimerTask;

/** A task to auto-save the config */
public class AutoSaveHandler extends TimerTask implements StarfishHandler {

  /** Create the task */
  public AutoSaveHandler() {
    if (this.getPreferences().getPreferenceOr("enabled", Boolean.class, false)) {
      new Timer()
          .schedule(
              this,
              this.getPreferences()
                  .getPreferenceOr("time", Time.class, Time.fromString("2m"))
                  .millis(),
              this.getPreferences()
                  .getPreferenceOr("time", Time.class, Time.fromString("2m"))
                  .millis());
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
  }
}
