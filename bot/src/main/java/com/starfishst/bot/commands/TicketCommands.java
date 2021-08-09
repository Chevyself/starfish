package com.starfishst.bot.commands;

import com.starfishst.api.Starfish;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.tickets.Offer;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.bot.handlers.ticket.TicketAnnouncementHandler;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.googas.commands.annotations.Free;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands for tickets */
public class TicketCommands {
  @Command(aliases = "new", description = "Create a new ticket")
  public Result newTicket(BotUser user) {
    try {
      Ticket ticket =
          Starfish.getTicketManager().createTicket(TicketType.TICKET_CREATOR, user, null);
      return new Result(user.getLocaleFile().get("ticket.new", ticket.getPlaceholders()));
    } catch (TicketCreationException e) {
      return new Result(ResultType.ERROR, e.getMessage());
    }
  }

  @Command(
      aliases = {"ticketinfo", "ti"},
      description = "ticket-info.desc",
      node = "starfish.ticket.info")
  public Result ticketinfo(
      CommandContext context,
      BotUser user,
      @Free(
              name = "Ticket",
              description = "The id of the ticket to see the information",
              suggestions = "-1")
          Ticket ticket) {
    Result.ResultBuilder builder = Result.builder();
    if (context.hasFlag("-u") && user.hasPermission("starfish.ticket.info.users", "discord")) {
      builder.getMessage().setEmbeds(ticket.toCompleteInformation(user, true).build());
    } else {
      builder.getMessage().setEmbeds(ticket.toCompleteInformation(user, false).build());
    }
    return builder.build();
  }

  @Command(
      aliases = {"announce", "repost", "reannounce"},
      description = "repost.desc",
      node = "starfish.ticket.announce")
  public Result announce(
      BotUser user,
      @Free(
              name = "id",
              description = "The id of the ticket to see the information",
              suggestions = "-1")
          Ticket ticket) {
    TicketType type = ticket.getType();
    TextChannel channel = type.getChannel();
    if (channel != null) {
      Starfish.getHandler(TicketAnnouncementHandler.class).announce(channel, user, ticket);
      Map<String, String> placeholders = ticket.getPlaceholders();
      placeholders.put("announce-channel", channel.getAsMention());
      return new Result(user.getLocaleFile().get("ticket.announced", placeholders));
    } else {
      return new Result(
          user.getLocaleFile().get("ticket.announce-failed", ticket.getPlaceholders()));
    }
  }

  /**
   * Updates the status of a ticket to closed
   *
   * @param sender the executor of the command
   * @param ticket the ticket that is queried to be closed
   * @return the result of the command execution
   */
  @Command(aliases = "close", description = "close.desc", node = "starfish.close")
  public Result close(
      BotUser sender,
      @Free(name = "close.ticket", description = "close.ticket.desc", suggestions = "-1")
          Ticket ticket) {
    if (ticket.getStatus() == TicketStatus.CLOSED) {
      return new Result(sender.getLocaleFile().get("closed.already", ticket.getPlaceholders()));
    } else {
      ticket.setStatus(TicketStatus.CLOSED);
      return new Result();
    }
  }

  @Command(aliases = "offers", description = "offers.desc")
  public Result offers(
      CommandContext context,
      BotUser sender,
      @Free(name = "offers.ticket", description = "offers.ticket.desc", suggestions = "-1")
          Ticket ticket) {
    LocaleFile locale = sender.getLocaleFile();
    if (ticket.getType() != TicketType.QUOTE) {
      return new Result(
          ResultType.ERROR, locale.get("offers.ticket-not-quote", ticket.getPlaceholders()));
    } else {
      boolean appendUsers = false;
      if (context.hasFlag("-u") && sender.hasPermission("offers.see-users", "discord")) {
        appendUsers = true;
      }
      Loader loader = Starfish.getLoader();
      Collection<Offer> offers = loader.getOffers(ticket);
      StringBuilder builder = new StringBuilder();
      builder.append(locale.get("offers.title", ticket.getPlaceholders()));
      for (Offer offer : offers) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("offer", offer.getOffer());
        if (appendUsers) {
          BotUser freelancer = loader.getStarfishUser(offer.getFreelancer());
          placeholders.putAll(freelancer.getPlaceholders());
          builder.append(locale.get("offers.offer-user", placeholders));
        } else {
          builder.append(locale.get("offers.offer", placeholders));
        }
      }
      return new Result(builder.toString());
    }
  }
}
