package com.starfishst.bot.tickets;

import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.loader.TicketManager;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketPreCreationEvent;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.utility.Discord;
import com.starfishst.bot.data.StarfishTicket;
import com.starfishst.bot.data.StarfishValuesMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

/** An implementation for {@link TicketManager} */
public class StarfishTicketManager implements TicketManager {

  /** The data loader */
  @NonNull private DataLoader loader;

  /** The configuration to keep track of the total of tickets and other stuff */
  @NonNull private Configuration configuration;

  /**
   * Create the ticket manager
   *
   * @param loader the data loader to get the tickets
   * @param configuration the configuration that the bot is using
   */
  public StarfishTicketManager(@NonNull DataLoader loader, @NonNull Configuration configuration) {
    this.loader = loader;
    this.configuration = configuration;
  }

  @Override
  public @NonNull Ticket createTicket(
      @NonNull TicketType type, @NonNull BotUser user, Ticket parent)
      throws TicketCreationException {
    TicketPreCreationEvent preCreationEvent = new TicketPreCreationEvent(this, type, user, parent);
    if (preCreationEvent.callAndGet()) {
      throw new TicketCreationException(preCreationEvent.getReason());
    } else {
      Category category = type.getCategory();
      Map<Long, String> users;
      if (parent != null) {
        users = parent.getUsersIdMap();
      } else {
        users = Maps.singleton(user.getId(), "owner");
      }
      StarfishValuesMap details = new StarfishValuesMap(new LinkedHashMap<>());
      if (parent != null) {
        details.addValues(parent.getDetails());
      }
      if (category != null) {
        StarfishTicket ticket =
            new StarfishTicket(
                this.getNewId(parent), type, details, TicketStatus.LOADING, users, -1);
        TextChannel channel;
        if (parent != null && parent.getTextChannel() != null) {
          parent.unload(false);
          channel = parent.getTextChannel();
          channel.getManager().setParent(category).queue();
        } else {
          channel =
              category
                  .createTextChannel(
                      user.getLocaleFile().get("ticket.channel-name", ticket.getPlaceholders()))
                  .complete();
        }
        ticket.setTextChannel(channel);
        if (user.getMember() != null) {
          Discord.allow(channel, user.getMember(), Discord.ALLOWED);
        }
        ticket.setTicketStatus(TicketStatus.CREATING);
        return ticket.cache();
      } else {
        throw new TicketCreationException(
            "The channel for the ticket could not be created. Has the guild been set yet?");
      }
    }
  }

  @Override
  public @NonNull DataLoader getDataLoader() {
    return this.loader;
  }

  @Override
  public long getTotal() {
    return this.configuration.getTotal();
  }

  @Override
  public void setDataLoader(@NonNull DataLoader loader) {
    this.loader = loader;
  }

  @Override
  public void setConfiguration(@NonNull Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void setTotal(long total) {
    this.configuration.setTotal(total);
  }
}
