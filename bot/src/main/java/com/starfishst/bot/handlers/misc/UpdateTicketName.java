package com.starfishst.bot.handlers.misc;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import java.util.Optional;
import lombok.NonNull;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;

/** Checks to update the name of a ticket channel */
public class UpdateTicketName implements StarfishHandler {

  /**
   * Listens for the status of a ticket being updated to change the name of its channel
   *
   * @param event the event of a ticket status being updated
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(@NonNull TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    ticket
        .getTextChannel()
        .ifPresent(
            channel -> {
              Optional<BotUser> optionalOwner = ticket.getOwner();
              LocaleFile file =
                  !optionalOwner.isPresent()
                      ? Starfish.getLanguageHandler().getFile("en")
                      : optionalOwner.get().getLocaleFile();
              channel
                  .getManager()
                  .setName(file.get("ticket.channel-name", ticket.getPlaceholders()));
            });
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "update-ticket-name";
  }
}
