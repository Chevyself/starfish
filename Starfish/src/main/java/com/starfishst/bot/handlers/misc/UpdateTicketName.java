package com.starfishst.bot.handlers.misc;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.StarfishEventHandler;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/** Checks to update the name of a ticket channel */
public class UpdateTicketName implements StarfishEventHandler {

  /**
   * Listens for the status of a ticket being updated to change the name of its channel
   *
   * @param event the event of a ticket status being updated
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(@NotNull TicketStatusUpdatedEvent event) {
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
