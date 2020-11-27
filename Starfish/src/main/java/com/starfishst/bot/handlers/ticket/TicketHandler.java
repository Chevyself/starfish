package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.console.Console;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import com.starfishst.jda.utils.message.MessageQuery;
import java.util.Map;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.time.ClassicTime;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/** A generic handler for tickets */
public class TicketHandler implements StarfishEventHandler {

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
            this.getPreferences().getValueOr("time-to-delete-closed-tickets", String.class, "15s");
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
            channel.delete().queueAfter(classicTime.getValue(), classicTime.getUnit());
          } catch (IllegalArgumentException e) {
            Console.exception(e, timeString + " is not valid time");
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
                    .getValueOr("announce-all-users-joining-leaving", Boolean.class, false)
            || owner != null && user.isFreelancer()) {
          EmbedQuery query = user.toCompleteInformation(owner);
          LocaleFile locale = owner.getLocaleFile();
          if (user.isFreelancer()) {
            query
                .getEmbedBuilder()
                .setTitle(locale.get("freelancer-joined-ticket.title", user.getPlaceholders()));
          } else {
            query
                .getEmbedBuilder()
                .setTitle(locale.get("user-joined-ticket.title", user.getPlaceholders()));
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
                    .getValueOr("announce-all-users-joining-leaving", Boolean.class, false)
            || owner != null && user.isFreelancer()) {
          EmbedQuery query = user.toCompleteInformation(owner);
          LocaleFile locale = owner.getLocaleFile();
          if (user.isFreelancer()) {
            query
                .getEmbedBuilder()
                .setTitle(locale.get("freelancer-left-ticket.title", user.getPlaceholders()));
          } else {
            query
                .getEmbedBuilder()
                .setTitle(locale.get("user-left-ticket.title", user.getPlaceholders()));
          }
          query.send(channel);
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
