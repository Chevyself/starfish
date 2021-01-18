package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketPreCreationEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.Messages;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import com.starfishst.jda.utils.message.MessageQuery;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import me.googas.commons.Lots;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.ClassicTime;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/** A generic handler for tickets */
public class TicketHandler implements StarfishHandler {

  @Getter private final Set<TicketType> bannedTypes = new HashSet<>();
  @Getter @Setter private int limit = 2;

  @Listener(priority = ListenPriority.LOWEST)
  public void onTicketPreCreation(TicketPreCreationEvent event) {
    if (event.isCancelled()) return;
    LocaleFile locale = event.getUser().getLocaleFile();
    if (event.getParent() == null && this.bannedTypes.contains(event.getType())) {
      event.setCancelled(true);
      event.setReason(
          locale.get(
              "tickets.banned-type",
              Maps.singleton("type", event.getType().toString().toLowerCase())));
      return;
    }
    Collection<Ticket> creating =
        Starfish.getLoader()
            .getTickets(
                event.getUser(), "owner", Lots.set(TicketType.values()), TicketStatus.CREATING);
    creating.removeIf(ticket -> ticket.getTextChannel() == null);
    if (event.getParent() == null
        && !creating.isEmpty()
        && !event.getUser().hasPermission("starfish.tickets.override-creating", "discord")) {
      event.setCancelled(true);
      event.setReason(locale.get("tickets.finish-creating"));
      return;
    }
    Collection<Ticket> open =
        Starfish.getLoader()
            .getTickets(
                event.getUser(),
                "owner",
                Lots.set(TicketType.values()),
                TicketStatus.CREATING,
                TicketStatus.OPEN,
                TicketStatus.LOADING);
    open.removeIf(
        ticket ->
            ticket.getTextChannel() == null
                || (event.getParent() != null && event.getParent().getId() == ticket.getId()));
    if (open.size() >= this.limit
        && !event.getUser().hasPermission("starfish.tickets.override-limit", "discord")) {
      event.setCancelled(true);
      event.setReason(
          locale.get(
              "tickets.limit",
              Maps.builder("limit", String.valueOf(this.limit))
                  .append("size", String.valueOf(open.size()))));
    }
  }

  /**
   * Listen when the status of a ticket is updated to apply some changes
   *
   * @param event the event of the status of a ticket being changed
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    if (!event.isCancelled()) {
      TextChannel channel = ticket.getTextChannel();
      BotUser owner = ticket.getOwner();
      if (channel != null && owner != null) {
        channel
            .getManager()
            .setName(owner.getLocaleFile().get("ticket.channel-name", ticket.getPlaceholders()))
            .queue();
      }
      Ticket child = Starfish.getTicketManager().getDataLoader().getTicket(ticket.getId());
      if (child == ticket) {
        child = null;
      }
      if (!event.isCancelled()
          && event.getStatus() == TicketStatus.CLOSED
          && child == null
          && channel != null
          && owner != null) {
        String timeString =
            this.getPreferences().getOr("time-to-delete-closed-tickets", String.class, "15s");
        if (!timeString.equalsIgnoreCase("none")) {
          try {
            LocaleFile file = owner.getLocaleFile();
            Time time = Time.fromString(timeString);
            ClassicTime classicTime = time.toClassicTime();
            Map<String, String> placeholders = ticket.getPlaceholders();
            placeholders.put("time", time.toEffectiveString());
            Messages.build(
                    file.get("ticket.closed.title", placeholders),
                    file.get("ticket.closed.desc", placeholders),
                    ResultType.GENERIC,
                    owner)
                .send(channel);
            channel.delete().queueAfter(classicTime.getValue(), classicTime.getUnit().toTimeUnit());
          } catch (IllegalArgumentException e) {
            Starfish.getFallback().process(e, timeString + " is not valid time");
          }
        }
      }
    }
  }

  /**
   * Listen to when an user gets added to a ticket
   *
   * @param event the event of an user being removed from a ticket
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketAddUser(TicketAddUserEvent event) {
    BotUser user = event.getUser();
    if (!event.isCancelled()) {
      TextChannel channel = event.getTicket().getTextChannel();
      Member member = user.getMember();
      BotUser owner = event.getTicket().getOwner();
      if (member != null && channel != null) {
        Discord.allow(channel, member, Discord.ALLOWED);
        if (owner != null
                && this.getPreferences()
                    .getOr("announce-all-users-joining-leaving", Boolean.class, false)
            || owner != null && user.isFreelancer()) {
          EmbedQuery query = user.toCompleteInformation(owner);
          LocaleFile locale = owner.getLocaleFile();
          if (user.isFreelancer()) {
            query.setTitle(locale.get("freelancer-joined-ticket.title", user.getPlaceholders()));
          } else {
            query.setTitle(locale.get("user-joined-ticket.title", user.getPlaceholders()));
          }
          if (user.isFreelancer()) {
            MessageQuery message = query.getAsMessageQuery();
            message.getBuilder().append(channel.getGuild().getPublicRole());
            message.send(channel);
          } else {
            query.send(channel);
          }
        }
      }
    }
  }

  /**
   * Listen to when an user gets removed from a ticket
   *
   * @param event the event of an user being removed from a ticket
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketRemoveUser(TicketRemoveUserEvent event) {
    BotUser user = event.getUser();
    if (!event.isCancelled()) {
      TextChannel channel = event.getTicket().getTextChannel();
      Member member = user.getMember();
      if (member != null && channel != null) {
        Discord.disallow(channel, member);
        BotUser owner = event.getTicket().getOwner();
        if (owner != null
                && this.getPreferences()
                    .getOr("announce-all-users-joining-leaving", Boolean.class, false)
            || owner != null && user.isFreelancer()) {
          EmbedQuery query = user.toCompleteInformation(owner);
          LocaleFile locale = owner.getLocaleFile();
          if (user.isFreelancer()) {
            query.setTitle(locale.get("freelancer-left-ticket.title", user.getPlaceholders()));
          } else {
            query.setTitle(locale.get("user-left-ticket.title", user.getPlaceholders()));
          }
          query.send(channel);
        }
      }
    }
  }

  @Override
  public void onEnable() {
    this.limit = this.getPreferences().getOr("open-limit", Double.class, 2D).intValue();
    for (Object banned : this.getPreferences().getList("banned")) {
      if (banned != null) {
        try {
          this.bannedTypes.add(TicketType.valueOf(banned.toString()));
        } catch (IllegalArgumentException e) {
          Starfish.getFallback().process(e, banned.toString() + " is not a valid ticket type");
        }
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "tickets";
  }
}
