package com.starfishst.bot.commands;

import com.starfishst.api.utility.Messages;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.data.messages.panel.TicketPanelMessage;
import com.starfishst.bot.handlers.lang.StarfishLocaleFile;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.message.MessagesFactory;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.exceptions.type.SimpleRuntimeException;
import com.starfishst.core.objects.JoinedNumber;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ContextException;

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

  @Command(aliases = "ticketPanel", description = "Create a ticket panel", permission = @Perm(node = "starfish.admin"))
  public Result ticketPanel(Message message, @Optional(name = "id", description = "The id of the ticket to become a ticket panel") JoinedNumber number){
    if (number != null) {
      try {
        System.out.println(number.asLong());
        message.getTextChannel().retrieveMessageById(number.asLong()).complete();
        return new Result();
      } catch (Exception e) {
        throw new SimpleRuntimeException("The given id is not valid", e);
      }
    } else {
      StarfishLocaleFile locale = Starfish.getLanguageHandler().getFile("en");
      return new Result(Messages.build(locale.get("ticket-panel.title"), locale.get("ticket-panel.description"), ResultType.GENERIC, locale), TicketPanelMessage::new);
    }
  }
}
