package com.starfishst.ethot.tasks;

import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.time.Time;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.util.Console;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
      Errors.addError("Bot could not be auto-saved");
    }
  }
}
