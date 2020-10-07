package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.console.Console;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.data.messages.order.ClaimOrderResponsiveMessage;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import com.starfishst.jda.utils.message.MessageQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.googas.commons.Lots;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/** Announces tickets */
public class TicketAnnouncementHandler implements StarfishEventHandler {

  /**
   * Listen to when a ticket is open to announce it
   *
   * @param event the event of a ticket having its status updated
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    TicketType type = ticket.getTicketType();
    if (!event.isCancelled() && event.getStatus() == TicketStatus.OPEN) {
      BotUser owner = ticket.getOwner();
      TextChannel textChannel = ticket.getTextChannel();
      if (this.getPreferences().getValueOr("pin-copy-in-ticket", Boolean.class, true)
          && textChannel != null
          && owner != null) {
        ticket.toCompleteInformation(owner, false).send(textChannel, msg -> msg.pin().queue());
      }
      if (owner != null && this.getAnnounceTypes().contains(type)) {
        TextChannel channel = type.getChannel();
        if (channel != null) {
          this.announce(channel, owner, ticket);
        }
      }
    }
  }

  /**
   * Announces a ticket
   *
   * @param channel the channel that will receive the announcement
   * @param user the user that will read the announcement
   * @param ticket the ticket that is being announced
   */
  @SuppressWarnings("unchecked")
  public void announce(
      @NotNull TextChannel channel, @NotNull BotUser user, @NotNull Ticket ticket) {
    List<IMentionable> toMention = new ArrayList<>();
    ticket
        .getDetails()
        .getMap()
        .forEach(
            (key, value) -> {
              // Safe to cast
              if (value instanceof List) {
                Class<?> clazz = Lots.getClazz((List<?>) value);
                if (clazz != null) {
                  if (Role.class.isAssignableFrom(clazz)) {
                    toMention.addAll(((List<Role>) value));
                  }
                }
              }
            });
    EmbedQuery embedQuery = ticket.toCompleteInformation(user, false);
    if (ticket.getTicketType() == TicketType.QUOTE) {
      String footer = this.getPreferences().getValueOr("quote-footer", String.class, "none");
      if (!footer.equalsIgnoreCase("none")) {
        embedQuery
            .getEmbedBuilder()
            .setFooter(user.getLocaleFile().get(footer, ticket.getPlaceholders()));
      }
    }
    MessageQuery query = embedQuery.getAsMessageQuery();
    query.getBuilder().append(Lots.pretty(Discord.getAsMention(toMention)));
    query.send(
        channel,
        msg -> {
          if (ticket.getTicketType() == TicketType.ORDER) {
            new ClaimOrderResponsiveMessage(
                msg, new StarfishValuesMap(Maps.singleton("id", ticket.getId())));
          }
        });
  }

  /**
   * Get the types of tickets that can be announced in a announcements channel
   *
   * @return the types of ticket that can be announced in a channel
   */
  @NotNull
  private Set<TicketType> getAnnounceTypes() {
    Set<TicketType> announceTypes = new HashSet<>();
    List<String> names = this.getPreferences().getLisValue("announce-types");
    for (String name : names) {
      try {
        announceTypes.add(TicketType.valueOf(name));
      } catch (IllegalArgumentException e) {
        Console.exception(e, "Announcer: " + name + " is not a valid Ticket Type");
      }
    }
    return announceTypes;
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "announcer";
  }
}
