package com.starfishst.bot.tickets;

import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.events.tickets.TicketPreCreationEvent;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.loader.TicketManager;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.data.StarfishTicket;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

/** An implementation for {@link TicketManager} */
public class StarfishTicketManager implements TicketManager {

  @NonNull @Getter @Setter private Loader dataLoader;
  @NonNull @Getter @Setter private Configuration configuration;

  /**
   * Create the ticket manager
   *
   * @param loader the data loader to get the tickets
   * @param configuration the configuration that the bot is using
   */
  public StarfishTicketManager(@NonNull Loader loader, @NonNull Configuration configuration) {
    this.dataLoader = loader;
    this.configuration = configuration;
  }

  private TextChannel getChannel(
      @NonNull BotUser user, Ticket parent, Category category, StarfishTicket ticket) {
    TextChannel channel;
    if (parent != null && parent.getTextChannel() != null) {
      channel = parent.getTextChannel();
      channel.getManager().setParent(category).queue();
    } else {
      channel =
          category
              .createTextChannel(
                  user.getLocaleFile().get("ticket.channel-name", ticket.getPlaceholders()))
              .complete();
    }
    return channel;
  }

  @NonNull
  private ValuesMap getDetails(Ticket parent) {
    ValuesMap details = new ValuesMap(new LinkedHashMap<>());
    if (parent != null) {
      details.add(parent.getDetails());
    }
    return details;
  }

  @NonNull
  private Map<Long, String> getUsers(@NonNull BotUser user, Ticket parent) {
    Map<Long, String> users;
    if (parent != null) {
      users = parent.getUsersIdMap();
    } else {
      users = Maps.singleton(user.getId(), "owner");
    }
    return users;
  }

  @Override
  public @NonNull Ticket createTicket(
      @NonNull TicketType type, @NonNull BotUser user, Ticket parent)
      throws TicketCreationException {
    TicketPreCreationEvent preCreationEvent = new TicketPreCreationEvent(this, type, user, parent);
    if (preCreationEvent.callAndGet())
      throw new TicketCreationException(preCreationEvent.getReason());
    Category category = type.getCategory();
    Map<Long, String> users = this.getUsers(user, parent);
    ValuesMap details = this.getDetails(parent);
    if (category != null) {
      if (parent != null) parent.unload(false);
      StarfishTicket ticket =
          new StarfishTicket(this.getNewId(parent), type, details, TicketStatus.LOADING, users, -1)
              .cache();
      TextChannel channel = this.getChannel(user, parent, category, ticket);
      ticket.setTextChannel(channel);
      if (user.getMember() != null) {
        Discord.allow(channel, user.getMember(), Discord.ALLOWED);
      }
      ticket.setStatus(TicketStatus.CREATING);
      return ticket;
    } else {
      throw new TicketCreationException(
          "The channel for the ticket could not be created. Has the guild been set yet?");
    }
  }

  @Override
  public long getTotal() {
    return this.configuration.getTotal();
  }

  @Override
  public void setTotal(long total) {
    this.configuration.setTotal(total);
  }
}
