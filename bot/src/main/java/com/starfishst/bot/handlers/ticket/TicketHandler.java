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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import me.googas.commands.jda.result.ResultType;
import me.googas.starbox.builders.MapBuilder;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import me.googas.starbox.time.Time;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
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
              Collections.singletonMap("type", event.getType().toString().toLowerCase())));
      return;
    }
    Collection<Ticket> creating =
        Starfish.getLoader()
            .getTickets(
                event.getUser(),
                "owner",
                Arrays.asList(TicketType.values()),
                TicketStatus.CREATING);
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
                new HashSet<>(Arrays.asList(TicketType.values())),
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
              MapBuilder.of("limit", String.valueOf(this.limit))
                  .put("size", String.valueOf(open.size()))));
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
      Optional<TextChannel> optionalChannel = ticket.getTextChannel();
      Optional<BotUser> optionalOwner = ticket.getOwner();
      if (optionalChannel.isPresent() && optionalOwner.isPresent()) {
        optionalChannel
            .get()
            .getManager()
            .setName(
                optionalOwner
                    .get()
                    .getLocaleFile()
                    .get("ticket.channel-name", ticket.getPlaceholders()))
            .queue();
      }
      Ticket child =
          Starfish.getTicketManager().getDataLoader().getTicket(ticket.getId()).orElse(null);
      if (child == ticket) {
        child = null;
      }
      if (!event.isCancelled()
          && event.getStatus() == TicketStatus.CLOSED
          && child == null
          && optionalChannel.isPresent()
          && optionalOwner.isPresent()) {
        TextChannel channel = optionalChannel.get();
        BotUser owner = optionalOwner.get();
        String timeString =
            this.getPreferences().getOr("time-to-delete-closed-tickets", String.class, "15s");
        if (!timeString.equalsIgnoreCase("none")) {
          try {
            LocaleFile file = owner.getLocaleFile();
            Time time = Time.parse(timeString, true);
            Map<String, String> placeholders = ticket.getPlaceholders();
            placeholders.put("time", time.toString());
            channel
                .sendMessage(
                    Messages.build(
                            file.get("ticket.closed.title", placeholders),
                            file.get("ticket.closed.desc", placeholders),
                            ResultType.GENERIC,
                            owner)
                        .build())
                .queue();
            channel.delete().queueAfter(time.toMillisRound(), TimeUnit.MILLISECONDS);
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
      Optional<TextChannel> optionalChannel = event.getTicket().getTextChannel();
      Optional<Member> optionalMember = user.getMember();
      Optional<BotUser> optionalOwner = event.getTicket().getOwner();
      if (optionalMember.isPresent() && optionalChannel.isPresent()) {
        Discord.allow(optionalChannel.get(), optionalMember.get(), Discord.ALLOWED);
        if (optionalOwner.isPresent()
                && this.getPreferences()
                    .getOr("announce-all-users-joining-leaving", Boolean.class, false)
            || optionalOwner.isPresent() && user.isFreelancer()) {
          TextChannel channel = optionalChannel.get();
          EmbedBuilder embed = user.toCompleteInformation(optionalOwner.get());
          LocaleFile locale = optionalOwner.get().getLocaleFile();
          if (user.isFreelancer()) {
            embed.setTitle(locale.get("freelancer-joined-ticket.title", user.getPlaceholders()));
          } else {
            embed.setTitle(locale.get("user-joined-ticket.title", user.getPlaceholders()));
          }
          if (user.isFreelancer()) {
            MessageBuilder messageBuilder =
                new MessageBuilder(embed).append(channel.getGuild().getPublicRole());
            channel.sendMessage(messageBuilder.build()).queue();
          } else {
            channel.sendMessageEmbeds(embed.build()).queue();
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
      Optional<TextChannel> optionalChannel = event.getTicket().getTextChannel();
      Optional<Member> optionalMember = user.getMember();
      if (optionalMember.isPresent() && optionalChannel.isPresent()) {
        TextChannel channel = optionalChannel.get();
        Discord.disallow(channel, optionalMember.get());
        Optional<BotUser> owner = event.getTicket().getOwner();
        if (owner.isPresent()
                && this.getPreferences()
                    .getOr("announce-all-users-joining-leaving", Boolean.class, false)
            || owner.isPresent() && user.isFreelancer()) {
          EmbedBuilder embed = user.toCompleteInformation(owner.get());
          LocaleFile locale = owner.get().getLocaleFile();
          if (user.isFreelancer()) {
            embed.setTitle(locale.get("freelancer-left-ticket.title", user.getPlaceholders()));
          } else {
            embed.setTitle(locale.get("user-left-ticket.title", user.getPlaceholders()));
          }
          channel.sendMessageEmbeds(embed.build()).queue();
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
