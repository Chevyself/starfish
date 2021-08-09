package com.starfishst.bot.handlers.misc;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import lombok.NonNull;
import me.googas.commands.jda.result.ResultType;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;
import net.dv8tion.jda.api.entities.TextChannel;

/** Cleans the discord server from unloaded tickets, etc. */
public class CleanerHandler extends TimerTask implements StarfishHandler {

  /** The map that contains the ticket id and the time left */
  @NonNull private final Map<Long, Long> map = new HashMap<>();

  public CleanerHandler() {
    new Timer().schedule(this, 0, 1000);
  }

  public void unload(@NonNull Ticket ticket) {
    ticket.setStatus(TicketStatus.CLOSED);
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
    if (this.getPreferences().getOr("delete-uncompleted-ticket-channels", Boolean.class, true)
        && ticket.getStatus() == TicketStatus.CREATING) {
      if (this.containsTime(time)) {
        BotUser owner = ticket.getOwner();
        TextChannel channel = ticket.getTextChannel();
        if (owner != null && channel != null) {
          Map<String, String> placeholders = Collections.singletonMap("time", time.toString());
          channel
              .sendMessage(
                  Messages.build(
                          owner.getLocaleFile().get("cleaner.delete-time.title", placeholders),
                          owner
                              .getLocaleFile()
                              .get("cleaner.delete-time.description", placeholders),
                          ResultType.GENERIC,
                          owner)
                      .build())
              .queue(Messages.getErrorConsumer());
        }
      }
    }
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    if (event.isCancelled()) return;
    if (event.getStatus() == TicketStatus.CREATING) {
      this.map.put(event.getTicket().getId(), this.getTime().getAs(Unit.SECONDS).getValueRound());
    }
    if (event.getStatus() != TicketStatus.CREATING) {
      this.map.remove(event.getTicket().getId());
    }
  }

  @NonNull
  public Ticket refresh(@NonNull Ticket ticket) {
    this.map.put(ticket.getId(), this.getTime().getAs(Unit.SECONDS).getValueRound());
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
    return Time.parse(this.getPreferences().getOr("to-delete-ticket", String.class, "3m"), false);
  }

  /**
   * Get the times to announce that a ticket channel will be deleted
   *
   * @return the times
   */
  @NonNull
  private List<Time> getTimes() {
    List<String> timeStrings = this.getPreferences().getList("time-to-announce-deletion");
    List<Time> times = new ArrayList<>();
    for (String timeString : timeStrings) {
      try {
        times.add(Time.parse(timeString, true));
      } catch (IllegalArgumentException e) {
        Starfish.getFallback().process(e, "Cleaner: " + timeString + " is not valid time!");
      }
    }
    return times;
  }

  @Override
  public void run() {
    HashMap<Long, Long> copy = new HashMap<>(this.map);
    copy.forEach(
        (id, secondsLeft) -> {
          long left = secondsLeft - 1;
          Ticket ticket = Starfish.getLoader().getTicket(id);
          this.map.put(id, left);
          if (ticket != null && ticket.getStatus() == TicketStatus.CREATING) {
            if (left > 0) {
              this.onSecondPass(ticket, Time.of(left, Unit.SECONDS));
            } else {
              this.unload(ticket);
            }
          }
        });
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "cleaner";
  }
}
