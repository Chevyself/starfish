package com.starfishst.bot.handlers.misc;

import com.starfishst.api.utility.console.Console;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.StarfishHandler;
import java.util.Timer;
import java.util.TimerTask;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;

/** A task to auto-save the config */
public class AutoSaveHandler extends TimerTask implements StarfishHandler {

  /** Create the task */
  public AutoSaveHandler() {
    if (this.getPreferences().getValueOr("enabled", Boolean.class, false)) {
      new Timer().schedule(this, this.getTime().millis(), this.getTime().millis());
    }
  }

  /**
   * Get the time to auto save
   *
   * @return the time to auto save
   */
  @NotNull
  private Time getTime() {
    try {
      return Time.fromString(this.getPreferences().getValueOr("time", String.class, "2m"));
    } catch (IllegalArgumentException e) {
      Console.exception(
          e,
          "Auto Save: "
              + this.getPreferences().getValue("time", String.class)
              + " is not valid time!");
      return new Time(2, Unit.MINUTES);
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
    System.out.println("Saving...");
    Starfish.save();
  }
}
