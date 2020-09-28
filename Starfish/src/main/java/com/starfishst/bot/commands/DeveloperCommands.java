package com.starfishst.bot.commands;

import com.starfishst.bot.Starfish;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.result.Result;

/** Commands used by the developer or server owner */
public class DeveloperCommands {

  /**
   * Stops the bot
   *
   * @return the result just says that the bot has been stopped
   */
  @Command(
      aliases = "stop",
      description = "Stops the bot",
      permission = @Perm(node = "starfish.admin"))
  public Result stop() {
    Starfish.stop();
    return new Result("Bot has been stopped");
  }
}
