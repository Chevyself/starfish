package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketAddDetailEvent;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.StarfishEventHandler;
import me.googas.commons.Strings;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;

/** Handles quotes */
public class QuoteHandler implements StarfishEventHandler {

  /**
   * Listen to when a detail is added to a ticket and check if a quote can be created
   *
   * @param event the event of a ticket can be created
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketAddDetailEvent(TicketAddDetailEvent event) {
    Ticket ticket = event.getTicket();
    BotUser owner = ticket.getOwner();
    TextChannel channel = ticket.getTextChannel();
    Object detail = event.getDetail();
    if (!event.isCancelled()
        && event.getSimple().startsWith("budget")
        && detail instanceof String
        && Strings.containsIgnoreCase((String) detail, "quote")
        && ticket.getTicketStatus() == TicketStatus.CREATING
        && ticket.getTicketType() != TicketType.QUOTE
        && owner != null) {
      try {
        Starfish.getTicketManager().createTicket(TicketType.QUOTE, owner, ticket);
        ticket.unload(false);
        ticket.unload();
      } catch (TicketCreationException e) {
        if (channel != null) {
          e.toQuery(owner).send(channel);
        }
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "quotes";
  }
}
