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
import java.util.Optional;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;

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
    Optional<BotUser> optionalOwner = ticket.getOwner();
    Object detail = event.getDetail();
    if (!event.isCancelled()
        && event.getSimple().startsWith("budget")
        && detail instanceof String
        && ((String) detail).toLowerCase().contains("quote")
        && ticket.getStatus() == TicketStatus.CREATING
        && ticket.getType() != TicketType.QUOTE
        && optionalOwner.isPresent()) {
      BotUser owner = optionalOwner.get();
      try {
        Starfish.getTicketManager().createTicket(TicketType.QUOTE, owner, ticket);
        try {
          ticket.unload(false);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      } catch (TicketCreationException e) {
        ticket
            .getTextChannel()
            .ifPresent(
                channel -> {
                  channel.sendMessage(e.toMessage().build()).queue(Messages.getErrorConsumer());
                });
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
