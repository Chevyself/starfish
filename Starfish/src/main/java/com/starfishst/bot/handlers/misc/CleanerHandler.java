package com.starfishst.bot.handlers.misc;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.utility.Messages;
import com.starfishst.jda.result.ResultType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import lombok.NonNull;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import net.dv8tion.jda.api.entities.TextChannel;

/** Cleans the discord server from unloaded tickets, etc. */
public class CleanerHandler extends TimerTask implements StarfishHandler {

  /** The map that contains the ticket id and the time left */
  @NonNull private final Map<Long, Long> map = new HashMap<>();

  public CleanerHandler() {
    new Timer().schedule(this, 0, 1000);
  }

  public void unload(@NonNull Ticket ticket) {
    ticket.setTicketStatus(TicketStatus.CLOSED);
    /*
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
     */
    this.map.remove(ticket.getId());
  }

  public void onSecondPass(@NonNull Ticket ticket, @NonNull Time time) {
    if (this.getPreferences().getValueOr("delete-uncompleted-ticket-channels", Boolean.class, true)
        && ticket.getTicketStatus() == TicketStatus.CREATING) {
      if (this.containsTime(time)) {
        BotUser owner = ticket.getOwner();
        TextChannel channel = ticket.getTextChannel();
        if (owner != null && channel != null) {
          Map<String, String> placeholders = Maps.singleton("time", time.toEffectiveString());
          Messages.build(
                  owner.getLocaleFile().get("cleaner.delete-time.title", placeholders),
                  owner.getLocaleFile().get("cleaner.delete-time.description", placeholders),
                  ResultType.GENERIC,
                  owner)
              .send(channel, Messages.getErrorConsumer());
        }
      }
    }
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    if (event.isCancelled()) return;
    if (event.getStatus() == TicketStatus.CREATING) {
      this.map.put(event.getTicket().getId(), this.getTime().getValue(Unit.SECONDS));
    }
    if (event.getStatus() != TicketStatus.CREATING) {
      this.map.remove(event.getTicket().getId());
    }
  }

  @NonNull
  public Ticket refresh(@NonNull Ticket ticket) {
    this.map.put(ticket.getId(), this.getTime().getValue(Unit.SECONDS));
    return ticket;
  }

  /**
   * Checks whether {@link #getTimes()} contains the time to compare
   *
   * @param compare the time to compare
   * @return true if times contains it
   */
  private boolean containsTime(@NonNull Time compare) {
    for (Time time : this.getTimes()) {
      if (time.equals(compare)) {
        return true;
      }
    }
    return false;
  }

  @NonNull
  public Time getTime() {
    return Time.fromString(
        this.getPreferences().getValueOr("to-delete-ticket", String.class, "3m"));
  }

  @Override
  public void run() {
    HashMap<Long, Long> copy = new HashMap<>(this.map);
    copy.forEach(
        (id, secondsLeft) -> {
          long left = secondsLeft - 1;
          Ticket ticket = Starfish.getLoader().getTicket(id);
          this.map.put(id, left);
          if (ticket != null && ticket.getTicketStatus() == TicketStatus.CREATING) {
            if (left > 0) {
              this.onSecondPass(ticket, new Time(left, Unit.SECONDS));
            } else {
              this.unload(ticket);
            }
          }
        });
  }

  /**
   * Get the times to announce that a ticket channel will be deleted
   *
   * @return the times
   */
  @NonNull
  private List<Time> getTimes() {
    List<String> timeStrings = this.getPreferences().getLisValue("time-to-announce-deletion");
    List<Time> times = new ArrayList<>();
    for (String timeString : timeStrings) {
      try {
        times.add(Time.fromString(timeString));
      } catch (IllegalArgumentException e) {
        Starfish.getFallback().process(e, "Cleaner: " + timeString + " is not valid time!");
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
