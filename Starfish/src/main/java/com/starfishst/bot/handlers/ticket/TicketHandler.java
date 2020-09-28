package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;

/** A generic handler for tickets */
public class TicketHandler implements StarfishEventHandler {

  /**
   * Listen when the status of a ticket is updated to apply some changes
   *
   * @param event the event of the status of a ticket being changed
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    if (!event.isCancelled()) {
      TextChannel channel = ticket.getTextChannel();
      BotUser owner = ticket.getOwner();
      if (channel != null && owner != null) {
        channel
            .getManager()
            .setName(owner.getLocaleFile().get("ticket.channel-name", ticket.getPlaceholders()))
            .queue();
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "tickets";
  }
}
