package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketAddDetailEvent;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import me.googas.commons.Strings;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;

/** Handles quotes */
public class QuoteHandler implements StarfishHandler {

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
        && ticket.getStatus() == TicketStatus.CREATING
        && ticket.getType() != TicketType.QUOTE
        && owner != null) {
      try {
        Starfish.getTicketManager().createTicket(TicketType.QUOTE, owner, ticket);
        try {
          ticket.unload(false);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      } catch (TicketCreationException e) {
        if (channel != null) {
          e.toQuery(owner).send(channel, Messages.getErrorConsumer());
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
