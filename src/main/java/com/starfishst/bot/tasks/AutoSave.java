package com.starfishst.bot.tasks;

import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.time.Time;
import com.starfishst.bot.Main;
import com.starfishst.bot.util.Console;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.jetbrains.annotations.NotNull;

/** A task to auto-save the config */
public class AutoSave extends TimerTask {

  /**
   * Create the task
   *
   * @param time the time to auto-save
   */
  public AutoSave(@NotNull Time time) {
    new Timer().schedule(this, time.millis(), time.millis());
  }

  @Override
  public void run() {
    try {
      Main.save();
      Console.info("Configuration saved successfully");
    } catch (IOException e) {
      e.printStackTrace();
      Fallback.addError("Bot could not be auto-saved");
    }
  }
}
