package com.starfishst.bot.commands;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.objects.invoicing.Fee;
import com.starfishst.bot.objects.management.AllowedTicketCloserChecker;
import com.starfishst.bot.objects.management.AllowedTicketManagerChecker;
import com.starfishst.bot.objects.responsive.type.archive.ArchiveResponsiveMessage;
import com.starfishst.bot.objects.responsive.type.panel.TicketPanel;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.loader.TicketLoader;
import com.starfishst.bot.tickets.type.QuestionsTicket;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.SimpleMath;
import com.starfishst.bot.util.Tickets;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Optional;
import com.starfishst.commands.annotations.Required;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedQuery;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.core.utils.Strings;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Some tickets commands are administrative others are also for public usage
 *
 * @author Chevy
 * @version 1.0.0
 */
public class TicketsCommand {

  /*
  /**
   * Creates a new ticket
   *
   * @param member the member creating the ticket
   * @return a successful result if the ticket was created
   * @throws DiscordManipulationException in case something related to Discord goes wrong. For
   *     example: if a category could not be created
   * @throws TicketCreationException in case something related to database or usage goes wrong
  @Command(aliases = "new", description = "Creates a new ticket", time = "10m")
  public Result newCommand(Member member, TextChannel channel)
      throws DiscordManipulationException, TicketCreationException {
    Ticket ticketByChannel = Main.getManager().getLoader().getTicketByChannel(channel.getIdLong());
    if (ticketByChannel != null) {
      return new Result(ResultType.USAGE, Lang.get("NOT_IN_TICKET_CHANNEL"));
    }
    Ticket ticket = Main.getManager().createTicket(TicketType.TICKET_CREATOR, member, null);
    if (ticket.getChannel() != null) {
      return new Result("Ticket created in " + ticket.getChannel().getAsMention());
    } else {
      return new Result("Ticket created... waiting for channel");
    }
  }
   */

  /**
   * Gives information about a ticket
   *
   * @param id the id of the ticket looking for info
   * @return the information of a ticket if it exists
   */
  @Command(aliases = "info", description = "Gives you the info about a Ticket")
  public Result info(
      @Required(name = "id", description = "The id of the ticket to get information") long id) {
    Ticket ticket = TicketManager.getInstance().getLoader().getTicket(id);
    if (ticket != null) {
      return new Result(
          ResultType.GENERIC,
          Messages.info(ticket),
          msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
    } else {
      return new Result(ResultType.ERROR, "Ticket not found");
    }
  }

  /**
   * Adds the mentioned members into a channel. Also if the channel is a ticket and one of the
   * mentioned members is a freelancer it will add them to the ticket
   *
   * @param user the user adding
   * @param message the message to get the members to
   * @param id the id of the ticket to announce
   * @return successful result if there's mentioned members
   */
  @Command(aliases = "repost", description = "Announces a ticket")
  public Result announce(
      AllowedTicketManagerChecker user,
      Message message,
      @Optional(name = "id", description = "The id of the ticket to announce", suggestions = "-1")
          long id) {
    TicketLoader loader = TicketManager.getInstance().getLoader();
    Ticket ticket;
    if (id == -1) {
      ticket = loader.getTicketByChannel(message.getTextChannel().getIdLong());
    } else {
      ticket = loader.getTicket(id);
    }
    if (ticket != null) {
      HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
      if (ticket instanceof QuestionsTicket) {
        ticket.onDone();
        return new Result(Lang.get("TICKET_ANNOUNCED", placeholders));
      } else {
        return new Result(ResultType.USAGE, Lang.get("NOT_ANNOUNCED", placeholders));
      }
    } else {
      return new Result(ResultType.USAGE, Lang.get("MENTION_TICKET_OR_CHANNEL"));
    }
  }

  /**
   * Creates an invoice embed
   *
   * @param checker the check to see if the checker is allowed to create invoices
   * @param channel the channel to generate the invoice
   * @param subtotal the subtotal of the service
   * @param strings the service
   * @return a successful result sending the invoice
   */
  @Command(aliases = "invoice", description = "Generates an invoice")
  public Result invoice(
      AllowedTicketManagerChecker checker,
      TextChannel channel,
      @Required(name = "subtotal", description = "The subtotal of the service") double subtotal,
      @Required(name = "service", description = "The service applying") JoinedStrings strings) {
    List<Fee> applyingFees = Configuration.getInstance().getApplyingFees(subtotal);
    double total = SimpleMath.getTotal(subtotal, applyingFees);
    Ticket ticket = TicketManager.getInstance().getLoader().getTicketByChannel(channel.getIdLong());
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("subtotal", String.format("%.02f", subtotal));
    placeholders.put("total", String.format("%.02f", total));
    placeholders.put("service", strings.getString());
    if (ticket != null) {
      placeholders.putAll(Tickets.getPlaceholders(ticket));
    }
    EmbedBuilder builder =
        Messages.create("INVOICE_TITLE", "INVOICE_DESCRIPTION", placeholders, placeholders)
            .getEmbedBuilder();

    applyingFees.forEach(
        fee -> {
          StringBuilder stringBuilder = Strings.getBuilder();
          HashMap<String, String> feePlaceholders = new HashMap<>();
          feePlaceholders.put("addition", String.valueOf(fee.getAddition()));
          feePlaceholders.put("percentage", String.valueOf(fee.getPercentage()));
          if (fee.getAddition() != 0) {
            stringBuilder.append(Lang.get("FEE_ADDITION", feePlaceholders));
          }
          if (fee.getPercentage() != 0) {
            stringBuilder.append(Lang.get("PERCENTAGE", feePlaceholders));
          }
          builder.addField(fee.getDescription(), stringBuilder.toString(), false);
        });

    return new Result(ResultType.GENERIC, new EmbedQuery(builder));
  }

  /**
   * Closes a ticket
   *
   * @param checker the check if the user can use the command
   * @param channel the channel to close
   * @return a successful result if the ticket is closing
   */
  @Command(aliases = "close", description = "Closes a ticket")
  public Result close(AllowedTicketCloserChecker checker, TextChannel channel) {
    Ticket ticket = TicketManager.getInstance().getLoader().getTicketByChannel(channel.getIdLong());
    if (ticket != null) {
      if (ticket.getStatus() != TicketStatus.CLOSED) {
        ticket.setStatus(TicketStatus.CLOSED);
        return new Result();
      } else {
        return new Result(
            ResultType.USAGE, Lang.get("ALREADY_CLOSED", Tickets.getPlaceholders(ticket)));
      }
    } else {
      return new Result(ResultType.USAGE, Lang.get("TICKET_NULL"));
    }
  }

  /**
   * Archives a ticket
   *
   * @param checker the check if the user can use the command
   * @param message the message to get the channel of the ticket
   * @return a successful result if the ticket is closing
   */
  @Command(aliases = "archive", description = "Archives a Ticket")
  public Result archive(AllowedTicketCloserChecker checker, Message message) {
    Ticket ticket =
        TicketManager.getInstance()
            .getLoader()
            .getTicketByChannel(message.getTextChannel().getIdLong());
    if (ticket != null) {
      HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
      if (ticket.getStatus() != TicketStatus.ARCHIVED) {
        return new Result(
            ResultType.GENERIC,
            Messages.create(
                "ARCHIVE_CONFIRM_TITLE", "ARCHIVE_CONFIRM_DESCRIPTION", placeholders, placeholders),
            ArchiveResponsiveMessage::new);
      } else {
        return new Result(ResultType.USAGE, Lang.get("ALREADY_ARCHIVED", placeholders));
      }
    } else {
      return new Result(ResultType.USAGE, Lang.get("TICKET_NULL"));
    }
  }

  /**
   * Creates a ticket panel
   *
   * @param checker to check if the user can create one
   * @return the created panel
   */
  @Command(
      aliases = {"ticketpanel", "tp"},
      description = "Creates a ticket creator panel")
  public Result ticketpanel(AllowedTicketCloserChecker checker) {
    return new Result(
        ResultType.GENERIC,
        Messages.create("TICKET_PANEL_TITLE", "TICKET_PANEL_DESCRIPTION", null, null),
        TicketPanel::new);
  }
}
