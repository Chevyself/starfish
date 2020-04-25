package com.starfishst.ethot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.util.Console;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.dv8tion.jda.api.Permission;

/**
 * Developer commands are not only used for 'debug'. They also control some aspects of the bot like
 * stopping and saving the configuration files.
 */
public class DevCommands {

  /**
   * Saves the bot configuration
   *
   * @return a success result in case the configuration was saved correctly. In case of an {@link
   *     IOException} it would be handled as sending an error result
   */
  @Command(
      aliases = "save",
      description = "Save bot configurations",
      permission = Permission.ADMINISTRATOR)
  public Result save() {
    try {
      Main.save();
      return new Result("Configuration successfully saved");
    } catch (IOException e) {
      Console.log(Level.SEVERE, e);
      Errors.addError(e.getMessage());
      return new Result(
          ResultType.ERROR,
          "There's been an error while trying to save the configuration",
          msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }
  }

  /**
   * Stops the bot
   *
   * @return no result
   */
  @Command(aliases = "stop", description = "Stops the bot", permission = Permission.ADMINISTRATOR)
  public Result stop() {
    Main.stop();
    return new Result();
  }
}
