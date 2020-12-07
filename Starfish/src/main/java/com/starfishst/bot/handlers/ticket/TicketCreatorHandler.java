package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.data.messages.creation.TicketCreatorMessage;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.message.MessageQuery;
import java.util.List;
import java.util.Map;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;

/** Handles ticket creators */
public class TicketCreatorHandler implements StarfishHandler {

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
      Map<String, String> placeholders = ticket.getPlaceholders();
      MessageQuery query =
          Messages.build(
                  locale.get("ticket-creator.title", placeholders),
                  locale.get("ticket-creator.description", placeholders),
                  ResultType.GENERIC,
                  owner)
              .getAsMessageQuery();
      List<BotUser> customers = ticket.getUsers("customer");
      for (BotUser customer : customers) {
        query.getBuilder().append(customer.getMention());
      }
      query.send(channel, msg -> new TicketCreatorMessage(msg, ticket.getId()).cache());
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "ticket-creators";
  }
}
