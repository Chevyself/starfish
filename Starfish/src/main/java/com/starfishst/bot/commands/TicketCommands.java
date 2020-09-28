package com.starfishst.bot.commands;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;

/** Commands for tickets */
public class TicketCommands {
  @Command(aliases = "new", description = "Create a new ticket")
  public Result newTicket(BotUser user) {
    try {
      Ticket ticket =
          Starfish.getTicketManager().createTicket(TicketType.TICKET_CREATOR, user, null);
      return new Result("Channel: " + ticket.getTextChannel().getAsMention());
    } catch (TicketCreationException e) {
      return new Result(ResultType.ERROR, e.getMessage());
    }
  }
}
