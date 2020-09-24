package com.starfishst.bot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;

/** Commands for tickets */
public class TicketCommands {
  @Command(aliases = "new", description = "Create a new ticket")
  public Result newTicket() {
    return new Result("TODO!");
  }
}
