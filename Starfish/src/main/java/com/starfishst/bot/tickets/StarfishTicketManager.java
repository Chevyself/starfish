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
import com.starfishst.core.utils.maps.Maps;
import java.util.HashMap;
import java.util.LinkedHashMap;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for {@link TicketManager} */
public class StarfishTicketManager implements TicketManager {

  /** The data loader */
  @NotNull private DataLoader loader;

  /** The configuration to keep track of the total of tickets and other stuff */
  @NotNull private Configuration configuration;

  /**
   * Create the ticket manager
   *
   * @param loader the data loader to get the tickets
   * @param configuration the configuration that the bot is using
   */
  public StarfishTicketManager(@NotNull DataLoader loader, @NotNull Configuration configuration) {
    this.loader = loader;
    this.configuration = configuration;
  }

  @Override
  public @NotNull Ticket createTicket(
      @NotNull TicketType type, @NotNull BotUser user, @Nullable Ticket parent)
      throws TicketCreationException {
    TicketPreCreationEvent preCreationEvent = new TicketPreCreationEvent(this, type, user, parent);
    if (preCreationEvent.callAndGet()) {
      throw new TicketCreationException(preCreationEvent.getReason());
    } else {
      Category category = type.getCategory();
      HashMap<BotUser, String> users;
      if (parent != null) {
        users = parent.getUsers();
      } else {
        users = Maps.singleton(user, "owner");
      }
      StarfishTicketDetails details = new StarfishTicketDetails(new LinkedHashMap<>());
      if (parent != null) {
        details.addValues(parent.getDetails());
      }
      if (category != null) {
        StarfishTicket ticket =
            new StarfishTicket(
                this.getNewId(parent), type, details, TicketStatus.LOADING, users, null);
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
        ticket.setTextChannel(channel);
        if (user.getMember() != null) {
          Discord.allow(channel, user.getMember(), Discord.ALLOWED);
        }
        ticket.setTicketStatus(TicketStatus.CREATING);
        return ticket;
      } else {
        throw new TicketCreationException(
            "The channel for the ticket could not be created. Has the guild been set yet?");
      }
    }
  }

  @Override
  public @NotNull DataLoader getDataLoader() {
    return this.loader;
  }

  @Override
  public long getTotal() {
    return this.configuration.getTotal();
  }

  @Override
  public void setDataLoader(@NotNull DataLoader loader) {
    this.loader = loader;
  }

  @Override
  public void setConfiguration(@NotNull Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void setTotal(long total) {
    this.configuration.setTotal(total);
  }
}
