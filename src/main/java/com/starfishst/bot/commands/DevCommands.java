package com.starfishst.bot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.bot.Main;
import com.starfishst.bot.util.Console;
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
      Fallback.addError(e.getMessage());
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

  /*
  /**
   * Create a verification message
   *
   * @return a new verification message in the channel executed
  @Command(
      aliases = {"verificationmsg", "vmsg"},
      description = "Creates a verification message",
      permission = Permission.ADMINISTRATOR)
  public Result verificationMsg() {
    return new Result(
        ResultType.GENERIC,
        Messages.create(
            "VERIFICATION_MESSAGE_TITLE", "VERIFICATION_MESSAGE_DESCRIPTION", null, null),
        VerificationResponsiveMessage::new);
  }
   */

  /*
  /**
   * Sends an announcement to the announcements channel
   *
   * @param strings the message to send
   * @return a successful result if the user uses the command correctly
   * @throws DiscordManipulationException in case that the channel doesn't exist and while trying to
   *     create it goes wrong

  @Command(
      aliases = "announce",
      description = "Sends a message to the announce channel",
      permission = Permission.ADMINISTRATOR)
  public Result announce(
      @Required(name = "message", description = "The message to announce") JoinedStrings strings)
      throws DiscordManipulationException {
    String string = strings.getString();
    if (string.contains("-d")) {
      TextChannel announcements =
          DiscordConfiguration.getInstance().getChannelByKey(Lang.get("CHANNEL_NAME_ANNOUNCE"));
      String[] split = string.split("-d");
      String title = split[0];
      String description = split[1];
      Messages.create(title, description).send(announcements);
      return new Result(
          Lang.get("ANNOUNCEMENT_SENT"), msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    } else {
      return new Result(
          ResultType.USAGE,
          Lang.get(
              "You must split the title and the description using '-d' to start the description"));
    }
  }
   */

}
