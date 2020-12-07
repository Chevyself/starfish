package com.starfishst.bot.handlers.misc;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import java.util.Timer;
import java.util.TimerTask;
import lombok.NonNull;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** A task to auto-save the config */
public class AutoSaveHandler extends TimerTask implements StarfishHandler {

  /**
   * Get the time to auto save
   *
   * @return the time to auto save
   */
  @NonNull
  private Time getTime() {
    try {
      return Time.fromString(this.getPreferences().getValueOr("time", String.class, "2m"));
    } catch (IllegalArgumentException e) {
      Starfish.getFallback()
          .process(
              e,
              "Auto Save: "
                  + this.getPreferences().getValue("time", String.class)
                  + " is not valid time!");
      return new Time(2, Unit.MINUTES);
    }
  }

  @Override
  public void onEnable() {
    if (this.getPreferences().getValueOr("enabled", Boolean.class, false)) {
      new Timer().schedule(this, this.getTime().millis(), this.getTime().millis());
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
