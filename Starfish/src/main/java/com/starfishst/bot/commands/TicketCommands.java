package com.starfishst.bot.commands;

import com.starfishst.api.data.tickets.Offer;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.ticket.TicketAnnouncementHandler;
import com.starfishst.bot.tickets.StarfishLoader;
import com.starfishst.core.annotations.Optional;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.Collection;
import java.util.HashMap;
import me.googas.commons.Strings;
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
      permission = @Perm(node = "starfish.ticket.info"))
  public Result ticketinfo(
      CommandContext context,
      BotUser user,
      @Optional(
              name = "Ticket",
              description = "The id of the ticket to see the information",
              suggestions = "-1")
          Ticket ticket) {
    if (context.hasFlag("-u") && user.hasPermission("starfish.ticket.info.users", "discord")) {
      return new Result(ticket.toCompleteInformation(user, true));
    } else {
      return new Result(ticket.toCompleteInformation(user, false));
    }
  }

  @Command(
      aliases = {"announce", "repost", "reannounce"},
      description = "repost.desc",
      permission = @Perm(node = "starfish.ticket.announce"))
  public Result announce(
      BotUser user,
      @Optional(
              name = "id",
              description = "The id of the ticket to see the information",
              suggestions = "-1")
          Ticket ticket) {
    TicketType type = ticket.getTicketType();
    TextChannel channel = type.getChannel();
    if (channel != null) {
      Starfish.getHandler(TicketAnnouncementHandler.class).announce(channel, user, ticket);
      HashMap<String, String> placeholders = ticket.getPlaceholders();
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
  @Command(
      aliases = "close",
      description = "close.desc",
      permission = @Perm(node = "starfish.close"))
  public Result close(
      BotUser sender,
      @Optional(name = "close.ticket", description = "close.ticket.desc", suggestions = "-1")
          Ticket ticket) {
    if (ticket.getTicketStatus() == TicketStatus.CLOSED) {
      return new Result(sender.getLocaleFile().get("closed.already", ticket.getPlaceholders()));
    } else {
      ticket.setTicketStatus(TicketStatus.CLOSED);
      return new Result();
    }
  }

  @Command(aliases = "offers", description = "offers.desc")
  public Result offers(
      CommandContext context,
      BotUser sender,
      @Optional(name = "offers.ticket", description = "offers.ticket.desc", suggestions = "-1")
          Ticket ticket) {
    LocaleFile locale = sender.getLocaleFile();
    if (ticket.getTicketType() != TicketType.QUOTE) {
      return new Result(
          ResultType.ERROR, locale.get("offers.ticket-not-quote", ticket.getPlaceholders()));
    } else {
      boolean appendUsers = false;
      if (context.hasFlag("-u") && sender.hasPermission("offers.see-users", "discord")) {
        appendUsers = true;
      }
      StarfishLoader loader = Starfish.getLoader();
      Collection<Offer> offers = loader.getOffers(ticket);
      StringBuilder builder = Strings.getBuilder();
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
