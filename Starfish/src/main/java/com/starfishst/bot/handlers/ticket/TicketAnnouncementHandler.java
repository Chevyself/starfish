package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.data.messages.order.ClaimOrderResponsiveMessage;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

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
    TicketType type = ticket.getTicketType();
    if (!event.isCancelled() && event.getStatus() == TicketStatus.OPEN) {
      BotUser owner = ticket.getOwner();
      TextChannel textChannel = ticket.getTextChannel();
      if (this.getPreferences().getValueOr("pin-copy-in-ticket", Boolean.class, true)
          && textChannel != null
          && owner != null) {
        ticket.toCompleteInformation(owner, false).send(textChannel, msg -> msg.pin().queue());
      }
      TextChannel channel = type.getChannel();
      if (channel != null && owner != null && this.getAnnounceTypes().contains(type)) {
        ticket
            .toCompleteInformation(owner, false)
            .send(
                channel,
                msg -> {
                  if (type == TicketType.ORDER) {
                    new ClaimOrderResponsiveMessage(
                        msg, new StarfishValuesMap(Maps.singleton("id", ticket.getId())));
                  }
                });
      }
    }
  }

  /**
   * Get the types of tickets that can be announced in a announcements channel
   *
   * @return the types of ticket that can be announced in a channel
   */
  @NotNull
  private Set<TicketType> getAnnounceTypes() {
    Set<TicketType> announceTypes = new HashSet<>();
    List<String> names = this.getPreferences().getLisValue("announce-types");
    for (String name : names) {
      try {
        announceTypes.add(TicketType.valueOf(name));
      } catch (IllegalArgumentException e) {
        Fallback.addError("Announcer: " + name + " is not a valid Ticket Type");
      }
    }
    return announceTypes;
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "announcer";
  }
}
