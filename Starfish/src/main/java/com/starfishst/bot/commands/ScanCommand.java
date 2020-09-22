package com.starfishst.bot.commands;

import com.mongodb.client.MongoCursor;
import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.bot.oldtickets.TicketManager;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.bot.oldtickets.loader.TicketLoader;
import com.starfishst.bot.oldtickets.loader.mongo.MongoTicketLoader;
import com.starfishst.bot.oldtickets.type.EmptyTicket;
import com.starfishst.bot.oldtickets.type.Ticket;
import com.starfishst.bot.util.Messages;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.simple.Async;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/** Scans stuff related to the database */
public class ScanCommand {

  /** The instance used to scan tickets */
  @NotNull private final TicketManager ticketManager;

  /**
   * Create an instance
   *
   * @param ticketManager the ticket manager to use
   */
  public ScanCommand(@NotNull TicketManager ticketManager) {
    this.ticketManager = ticketManager;
  }

  /**
   * Show the subcommands
   *
   * @return a message saying which are the subcommands
   */
  @Parent
  @Command(
      aliases = "scan",
      description = "Scan certain tickets in the database",
      permission = Permission.ADMINISTRATOR)
  public Result scan() {
    return new Result(ResultType.USAGE, Lang.get("SCAN_USE_SUBCOMMAND"));
  }

  /**
   * Scan for tickets that might have broken
   *
   * @param channel where the command was executed
   * @return a success result if everything went ok
   */
  @Command(
      aliases = "open",
      description = "Will look for broken open Tickets",
      permission = Permission.ADMINISTRATOR)
  public Result open(TextChannel channel) {
    TicketLoader loader = ticketManager.getLoader();
    List<Long> ids = new ArrayList<>();
    if (loader instanceof MongoTicketLoader) {
      new Async(
          () -> {
            MongoCursor<Document> cursor =
                ((MongoTicketLoader) loader).getDatabase().getCollection("tickets").find().cursor();
            while (cursor.hasNext()) {
              Ticket ticket = ((MongoTicketLoader) loader).getTicketFromDocument(cursor.next());
              if (ticket.getType() == TicketType.TICKET_CREATOR) {
                ids.add(ticket.getId());
                loader.saveTicket(
                    new EmptyTicket(
                        ticket.getId(),
                        ticket.getUser(),
                        ticket.getStatus(),
                        ticket.getChannel(),
                        ticket.getPayments()));
              } else if (ticket.getStatus() == TicketStatus.OPEN
                  || ticket.getStatus() == TicketStatus.ARCHIVED
                  || ticket.getStatus() == TicketStatus.CREATING) {
                if (ticket.getChannel() == null) {
                  ids.add(ticket.getId());
                  ticket.setStatus(TicketStatus.CLOSED);
                  loader.saveTicket(ticket);
                }
              }
            }
            if (ids.isEmpty()) {
              Messages.create("TITLE_GENERIC", "SCAN_OPEN_NO_TICKETS_CHANGED", null, null)
                  .send(channel);
            } else {
              HashMap<String, String> placeholders = Maps.singleton("ids", Lots.pretty(ids));
              Messages.create(
                      "TITLE_GENERIC", "SCAN_OPEN_TICKETS_CHANGED", placeholders, placeholders)
                  .send(channel);
            }
          });
    }
    return new Result();
  }
}
