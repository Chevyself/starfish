package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.messages.TicketCreatorReactionResponse;
import java.util.List;
import java.util.Map;
import me.googas.commands.jda.result.ResultType;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import net.dv8tion.jda.api.MessageBuilder;
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
        && ticket.getType() == TicketType.TICKET_CREATOR
        && owner != null) {
      LocaleFile locale = owner.getLocaleFile();
      Map<String, String> placeholders = ticket.getPlaceholders();
      MessageBuilder builder =
          new MessageBuilder(
              Messages.build(
                  locale.get("ticket-creator.title", placeholders),
                  locale.get("ticket-creator.description", placeholders),
                  ResultType.GENERIC,
                  owner));
      List<BotUser> customers = ticket.getUsers("customer");
      for (BotUser customer : customers) {
        builder.append(customer.getMention());
      }
      channel
          .sendMessage(builder.build())
          .queue(
              msg -> {
                TicketCreatorReactionResponse.add(
                    new BotResponsiveMessage(
                            msg.getIdLong(), new ValuesMap("ticket", ticket.getId()))
                        .cache(),
                    msg);
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
