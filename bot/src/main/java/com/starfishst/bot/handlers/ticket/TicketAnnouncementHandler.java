package com.starfishst.bot.handlers.ticket;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.messages.ClaimOrderReactionResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.TextChannel;

/** Announces tickets */
public class TicketAnnouncementHandler implements StarfishHandler {

  /**
   * Listen to when a ticket is open to announce it
   *
   * @param event the event of a ticket having its status updated
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    TicketType type = ticket.getType();
    if (!event.isCancelled() && event.getStatus() == TicketStatus.OPEN) {
      BotUser owner = ticket.getOwner();
      TextChannel textChannel = ticket.getTextChannel();
      if (this.getPreferences().getOr("pin-copy-in-ticket", Boolean.class, true)
          && textChannel != null
          && owner != null) {
        textChannel
            .sendMessageEmbeds(ticket.toCompleteInformation(owner, false).build())
            .queue(msg -> msg.pin().queue());
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
  public void announce(
      @NonNull TextChannel channel, @NonNull BotUser user, @NonNull Ticket ticket) {
    List<IMentionable> toMention = new ArrayList<>();
    ticket
        .getDetails()
        .getMap()
        .forEach(
            (key, value) -> {
              // Safe to cast
              if (value instanceof List) {
                Class<?> clazz = ValuesMap.getClazz((List<?>) value);
                if (clazz != null) {
                  if (Long.class.isAssignableFrom(clazz)) {
                    toMention.addAll(Discord.getRoles(ticket.getDetails().getList(key)));
                  }
                }
              }
            });
    EmbedBuilder builder = ticket.toCompleteInformation(user, false);
    if (ticket.getType() == TicketType.QUOTE) {
      String footer = this.getPreferences().getOr("quote-footer", String.class, "none");
      if (!footer.equalsIgnoreCase("none")) {
        builder.setFooter(user.getLocaleFile().get(footer, ticket.getPlaceholders()));
      }
    }
    MessageBuilder messageBuilder =
        new MessageBuilder(builder).append(ValuesMap.pretty(Discord.getAsMention(toMention)));
    channel
        .sendMessage(messageBuilder.build())
        .queue(
            msg -> {
              if (ticket.getType() == TicketType.ORDER) {
                new BotResponsiveMessage(
                        msg,
                        new HashSet<>(Arrays.asList(new ClaimOrderReactionResponse(null))),
                        new ValuesMap("id", ticket.getId()))
                    .cache()
                    .update();
              }
            });
  }

  /**
   * Get the types of tickets that can be announced in a announcements channel
   *
   * @return the types of ticket that can be announced in a channel
   */
  @NonNull
  private Set<TicketType> getAnnounceTypes() {
    Set<TicketType> announceTypes = new HashSet<>();
    List<String> names = this.getPreferences().getList("announce-types");
    for (String name : names) {
      try {
        announceTypes.add(TicketType.valueOf(name));
      } catch (IllegalArgumentException e) {
        Starfish.getFallback().process(e, "Announcer: " + name + " is not a valid Ticket Type");
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
