package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketNewChannelEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.NonNull;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

/** Represents a StarfishTicket. TODO This documentation has to be a little bit more done. */
public class StarfishTicket implements Ticket {

  /** The id of the ticket */
  private final long id;

  /** The type of ticket */
  @NonNull private final TicketType type;
  /** The map of users inside the ticket */
  @NonNull private final Map<Long, String> users;

  /** The details of the ticket */
  @NonNull private final StarfishValuesMap details;

  /** The channel where the ticket is happening */
  private long channel;
  /** The status of the ticket */
  @NonNull private TicketStatus status;

  /**
   * Create the ticket
   *
   * @param id the id of the ticket
   * @param type the type of the ticket
   * @param details the details of the ticket
   * @param status the status of the ticket
   * @param users the status that are inside the ticket
   * @param channel the channel where the ticket is occurring
   */
  public StarfishTicket(
      long id,
      @NonNull TicketType type,
      @NonNull StarfishValuesMap details,
      @NonNull TicketStatus status,
      @NonNull Map<Long, String> users,
      long channel) {
    this.id = id;
    this.type = type;
    this.details = details;
    this.status = status;
    this.users = users;
    this.channel = channel;
  }

  /**
   * Check whether an user with a role is in this ticket
   *
   * @param user the user to check if it is inside the ticket
   * @param role the role to check
   * @return true if the user and the given role is inside the ticket
   */
  public boolean hasUser(BotUser user, String role) {
    if (user == null || role == null) return false;
    AtomicBoolean bol = new AtomicBoolean(false);
    this.getUsersIdMap()
        .forEach(
            (id, userRole) -> {
              if (id != null) {
                if (id == user.getId() && userRole.equalsIgnoreCase(role)) {
                  bol.set(true);
                }
              }
            });
    return bol.get();
  }

  @Override
  public void onRemove() {
    new TicketUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().toUnloadTickets();
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NonNull TicketType getTicketType() {
    return this.type;
  }

  @Override
  public @NonNull Map<Long, String> getUsersIdMap() {
    return this.users;
  }

  @Override
  public @NonNull StarfishValuesMap getDetails() {
    return this.details;
  }

  @Override
  public TextChannel getTextChannel() {
    JDA jda = Starfish.getJdaConnection().getJda();
    if (jda != null) {
      return jda.getTextChannelById(this.channel);
    }
    return null;
  }

  @Override
  public @NonNull TicketStatus getTicketStatus() {
    return this.status;
  }

  @Override
  public boolean addUser(@NonNull BotUser user, @NonNull String role) {
    if (!new TicketAddUserEvent(this, user, role).callAndGet()) {
      this.users.put(user.getId(), role);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeUser(@NonNull BotUser user) {
    if (!new TicketRemoveUserEvent(this, user).callAndGet()) {
      this.users.remove(user.getId());
      return true;
    }
    return false;
  }

  @Override
  public long getTextChannelId() {
    return channel;
  }

  @Override
  public void setTextChannel(TextChannel channel) {
    if (!new TicketNewChannelEvent(this, channel).callAndGet()) {
      this.channel = channel == null ? -1 : channel.getIdLong();
    }
  }

  @Override
  public void setTicketStatus(@NonNull TicketStatus status) {
    if (!new TicketStatusUpdatedEvent(this, status).callAndGet()) {
      this.status = status;
    }
  }

  @Override
  public @NonNull StarfishTicket cache() {
    return (StarfishTicket) Ticket.super.cache();
  }

  @Override
  public String toString() {
    return "StarfishTicket{"
        + "id="
        + id
        + ", type="
        + type
        + ", users="
        + users
        + ", details="
        + details
        + ", channel="
        + channel
        + ", status="
        + status
        + "} ";
  }
}
