package com.starfishst.bot.commands;

import com.starfishst.api.utility.Messages;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.data.messages.panel.TicketPanelMessage;
import com.starfishst.bot.handlers.lang.StarfishLocaleFile;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.exceptions.type.SimpleRuntimeException;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import net.dv8tion.jda.api.entities.Message;

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

  @Command(
      aliases = "ticketPanel",
      description = "Create a ticket panel",
      permission = @Perm(node = "starfish.admin"))
  public Result ticketPanel(
      Message message,
      @Optional(
              name = "id",
              description = "The id of the ticket to become a ticket panel",
              suggestions = "-1")
          long number) {
    if (number != -1) {
      try {
        System.out.println(number);
        message.getTextChannel().retrieveMessageById(number).complete();
        return new Result();
      } catch (Exception e) {
        throw new SimpleRuntimeException("The given id is not valid", e);
      }
    } else {
      StarfishLocaleFile locale = Starfish.getLanguageHandler().getFile("en");
      return new Result(
          Messages.build(
              locale.get("ticket-panel.title"),
              locale.get("ticket-panel.description"),
              ResultType.GENERIC,
              locale),
          TicketPanelMessage::new);
    }
  }
}
