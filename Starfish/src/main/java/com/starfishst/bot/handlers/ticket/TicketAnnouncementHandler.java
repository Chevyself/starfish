package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.bot.data.StarfishPreferences;
import com.starfishst.bot.data.messages.order.ClaimOrderResponsiveMessage;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;

/** Announces tickets */
public class TicketAnnouncementHandler implements StarfishEventHandler {

  /**
   * Listen to when a ticket is open to announce it
   *
   * @param event the event of a ticket having its status updated
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    if (!event.isCancelled() && event.getStatus() == TicketStatus.OPEN) {
      BotUser owner = ticket.getOwner();
      TextChannel textChannel = ticket.getTextChannel();
      if (this.getPreferences().getPreferenceOr("pin-copy-in-ticket", Boolean.class, true)
          && textChannel != null
          && owner != null) {
        ticket.toCompleteInformation(owner, false).send(textChannel, msg -> msg.pin().queue());
      }
      TextChannel channel = ticket.getTicketType().getChannel();
      if (channel != null && owner != null) {
        ticket
            .toCompleteInformation(owner, false)
            .send(
                channel,
                msg ->
                    new ClaimOrderResponsiveMessage(
                        msg, new StarfishPreferences(Maps.singleton("id", ticket.getId()))));
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "announcer";
  }
}
