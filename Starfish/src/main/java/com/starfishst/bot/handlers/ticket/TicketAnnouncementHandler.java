package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;

/** Announces tickets */
public class TicketAnnouncementHandler implements StarfishEventHandler {

  /**
   * Listen to when a ticket is open to announce it
   * @param event the event of a ticket having its status updated
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    if (!event.isCancelled() && event.getStatus() == TicketStatus.OPEN) {
      BotUser owner = event.getTicket().getOwner();
      TextChannel textChannel = event.getTicket().getTextChannel();
      if (this.getPreferences().getPreferenceOr("pin-copy-in-ticket", Boolean.class, true)
          && textChannel != null
          && owner != null) {
        event.getTicket().toCompleteInformation(owner, false).send(textChannel, msg -> {
          msg.pin().queue();
        });
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
