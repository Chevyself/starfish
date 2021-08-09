package com.starfishst.bot.commands;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.messages.TicketPanelReactionResponse;
import me.googas.commands.annotations.Free;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import net.dv8tion.jda.api.entities.Message;

/** Commands used by the developer or server owner */
public class DeveloperCommands {

  /**
   * Stops the bot
   *
   * @return the result just says that the bot has been stopped
   */
  @Command(aliases = "stop", description = "Stops the bot", node = "starfish.admin")
  public Result stop() {
    Starfish.stop();
    return new Result("Bot has been stopped");
  }

  @Command(aliases = "ticketPanel", description = "Create a ticket panel", node = "starfish.admin")
  public Result ticketPanel(
      Message message,
      @Free(
              name = "id",
              description = "The id of the ticket to become a ticket panel",
              suggestions = "-1")
          long number) {
    if (number != -1) {
      try {
        message = message.getTextChannel().retrieveMessageById(number).complete();
      } catch (Exception e) {
        return Result.forType(ResultType.USAGE)
            .setDescription("The given id is not a valid id")
            .build();
      }
    }
    LocaleFile locale = Starfish.getLanguageHandler().getDefault();
    Result.ResultBuilder builder = Result.builder();
    builder
        .getMessage()
        .setEmbeds(
            Messages.build(
                    locale.get("ticket-panel.title"),
                    locale.get("ticket-panel.description"),
                    ResultType.GENERIC,
                    locale)
                .build());
    return builder
        .next(
            msg -> {
              BotResponsiveMessage responsiveMessage =
                  new BotResponsiveMessage(msg.getIdLong()).cache();
              responsiveMessage.addReactionResponse(
                  new TicketPanelReactionResponse(responsiveMessage), msg);
            })
        .build();
  }
}
