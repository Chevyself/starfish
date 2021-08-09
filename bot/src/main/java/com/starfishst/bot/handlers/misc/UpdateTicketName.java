package com.starfishst.bot.handlers.misc;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import lombok.NonNull;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;

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
    TextChannel textChannel = ticket.getTextChannel();
    BotUser owner = ticket.getOwner();
    if (textChannel != null) {
      LocaleFile file =
          owner == null ? Starfish.getLanguageHandler().getFile("en") : owner.getLocaleFile();
      textChannel.getManager().setName(file.get("ticket.channel-name", ticket.getPlaceholders()));
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "update-ticket-name";
  }
}
