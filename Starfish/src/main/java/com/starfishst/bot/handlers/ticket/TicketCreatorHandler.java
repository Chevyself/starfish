package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.data.messages.creation.TicketCreatorMessage;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.commands.result.ResultType;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;

/** Handles ticket creators */
public class TicketCreatorHandler implements StarfishEventHandler {

  /**
   * Listen to when a ticket gets its status updated
   *
   * @param event the event of a ticket status being updated
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    TextChannel channel = ticket.getTextChannel();
    BotUser owner = ticket.getOwner();
    if (!event.isCancelled()
        && event.getStatus() == TicketStatus.CREATING
        && channel != null
        && ticket.getTicketType() == TicketType.TICKET_CREATOR
        && owner != null) {
      LocaleFile locale = owner.getLocaleFile();
      HashMap<String, String> placeholders = ticket.getPlaceholders();
      Messages.build(
              locale.get("ticket-creator.title", placeholders),
              locale.get("ticket-creator.description", placeholders),
              ResultType.GENERIC,
              owner)
          .send(
              channel,
              msg -> {
                new TicketCreatorMessage(msg, ticket.getId());
              });
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "ticket-creators";
  }
}
