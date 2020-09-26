package com.starfishst.bot.commands;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.tickets.exception.TicketCreationException;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;

/** Commands for tickets */
public class TicketCommands {
  @Command(aliases = "new", description = "Create a new ticket")
  public Result newTicket(BotUser user) throws TicketCreationException {
    Ticket ticket = Starfish.getTicketManager().createTicket(TicketType.ORDER, user, null);
    return new Result("Channel: " + ticket.getTextChannel().getAsMention());
  }
}
