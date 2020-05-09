package com.starfishst.bot.commands;

import com.starfishst.bot.exception.CopyProcessException;
import com.starfishst.bot.legacy.CopyLegacyProcess;
import com.starfishst.bot.legacy.CopyLegacyProcessResult;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Exclude;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.Nullable;

/** The commands to for {@link com.starfishst.bot.legacy.CopyLegacyProcess} */
public class CopyCommands {

  /** The current process running in the command */
  @Nullable private CopyLegacyProcess process;

  /**
   * Starts the process if there's not one yet
   *
   * @param database the old database to get the tickets from
   * @return a success result if the process has started
   * @throws CopyProcessException in case that starting the process goes wrong
   */
  @Parent
  @Command(
      aliases = "copyLegacy",
      description = "Copies the tickets from Starfish 2.0",
      permission = Permission.ADMINISTRATOR)
  public Result copy(
      @Required(
              name = "database",
              description = "The name of the old database to copy the tickets from")
          String database)
      throws CopyProcessException {
    if (process != null) {
      return new Result(ResultType.ERROR, "Proccess is currently running");
    } else {
      process = new CopyLegacyProcess(TicketManager.getInstance(), database);
      process.start();
      return new Result(
          "Process has started check the console or /logs/ when finished for details");
    }
  }

  /**
   * Get the result of the process
   *
   * @return the result of the process
   */
  @Exclude
  @Command(
      aliases = "result",
      description = "Gives the result of the process",
      permission = Permission.ADMINISTRATOR)
  public Result result() {
    if (process != null && process.getTask() != null) {
      if (process.getTask().getResult() == CopyLegacyProcessResult.RUNNING) {
        return new Result(ResultType.ERROR, "The process is still running");
      } else {
        return new Result(
            "[" + process.getTask().getResult() + "] " + process.getTask().getResultMessage());
      }
    } else {
      return new Result("There's no process running at the moment");
    }
  }

  /**
   * Resets the {@link CopyCommands#process}
   *
   * @return success if it could have been stopped
   */
  @Command(
      aliases = "reset",
      description = "Resets the process",
      permission = Permission.ADMINISTRATOR)
  public Result reset() {
    if (process != null
        && process.getTask() != null
        && process.getTask().getResult() == CopyLegacyProcessResult.RUNNING) {
      return new Result(
          ResultType.ERROR, "You cannot reset the process when it is currently running");
    } else {
      process = null;
      return new Result("The process has been reset");
    }
  }
}
