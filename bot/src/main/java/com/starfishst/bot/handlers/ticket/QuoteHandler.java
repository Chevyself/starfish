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
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
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
        && ((String) detail).toLowerCase().contains("quote")
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
          channel.sendMessage(e.toQuery(owner).build()).queue(Messages.getErrorConsumer());
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
