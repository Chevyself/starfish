package com.starfishst.bot.handlers.misc;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketSecondPassEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.console.Console;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.jda.result.ResultType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/** Cleans the discord server from unloaded tickets, etc. */
public class CleanerHandler implements StarfishEventHandler {

  /**
   * Listens for the ticket being unloaded to check if it was creating to set it as closed and
   * delete its channel if the preference is set as so
   *
   * @param event the event of a ticket being unloaded
   */
  @Listener(priority = ListenPriority.LOWEST)
  public void onTicketUnloadedEvent(TicketUnloadedEvent event) {
    Ticket ticket = event.getTicket();
    if (ticket.getTicketStatus() == TicketStatus.CREATING) {
      ticket.setTicketStatus(TicketStatus.CLOSED);
      TextChannel channel = ticket.getTextChannel();
      if (channel != null
          && this.getPreferences()
              .getValueOr("delete-uncompleted-ticket-channels", Boolean.class, true)) {
        Ticket child = Starfish.getTicketManager().getDataLoader().getTicket(ticket.getId());
        if (child == ticket) {
          child = null;
        }
        if (child == null || !channel.equals(child.getTextChannel())) {
          channel.delete().queue();
          ticket.setTextChannel(null);
        }
      }
    }
  }

  /**
   * Listens for the seconds passed in a ticket to announce that it will be deleted
   *
   * @param event the event of a second passed for a ticket to be unloaded
   */
  @Listener(priority = ListenPriority.MEDIUM)
  public void onTicketSecondPass(TicketSecondPassEvent event) {
    if (this.getPreferences().getValueOr("delete-uncompleted-ticket-channels", Boolean.class, true)
        && event.getTicket().getTicketStatus() == TicketStatus.CREATING) {
      if (this.containsTime(event.getTimeLeft())) {
        BotUser owner = event.getTicket().getOwner();
        TextChannel channel = event.getTicket().getTextChannel();
        if (owner != null && channel != null) {
          HashMap<String, String> placeholders =
              Maps.singleton("time", event.getTimeLeft().toEffectiveString());
          Messages.build(
                  owner.getLocaleFile().get("cleaner.delete-time.title", placeholders),
                  owner.getLocaleFile().get("cleaner.delete-time.description", placeholders),
                  ResultType.GENERIC,
                  owner)
              .send(channel);
        }
      }
    }
  }

  /**
   * Checks whether {@link #getTimes()} contains the time to compare
   *
   * @param compare the time to compare
   * @return true if times contains it
   */
  private boolean containsTime(@NotNull Time compare) {
    for (Time time : this.getTimes()) {
      if (time.equals(compare)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the times to announce that a ticket channel will be deleted
   *
   * @return the times
   */
  @NotNull
  private List<Time> getTimes() {
    List<String> timeStrings = this.getPreferences().getLisValue("time-to-announce-deletion");
    List<Time> times = new ArrayList<>();
    for (String timeString : timeStrings) {
      try {
        times.add(Time.fromString(timeString));
      } catch (IllegalArgumentException e) {
        Console.exception(e, "Cleaner: " + timeString + " is not valid time!");
      }
    }
    return times;
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "cleaner";
  }
}
