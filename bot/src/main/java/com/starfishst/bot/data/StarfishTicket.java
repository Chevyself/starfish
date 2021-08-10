package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketNewChannelEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.ValuesMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.NonNull;
import me.googas.starbox.time.Time;
import net.dv8tion.jda.api.entities.TextChannel;

/** Represents a StarfishTicket. TODO This documentation has to be a little bit more done. */
public class StarfishTicket implements Ticket {

  @Getter private final long id;
  @NonNull @Getter private final TicketType type;
  @NonNull private final Map<Long, String> users;
  @NonNull @Getter private final ValuesMap details;
  @Getter private long channel;
  @NonNull @Getter private TicketStatus status;

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
      @NonNull ValuesMap details,
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
    return Starfish.getConfiguration().getUnloadTickets();
  }

  @Override
  public @NonNull Map<Long, String> getUsersIdMap() {
    return this.users;
  }

  @Override
  @NonNull
  public Optional<TextChannel> getTextChannel() {
    return Starfish.getJdaConnection().getJda().map(jda -> jda.getTextChannelById(this.channel));
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
    return this.channel;
  }

  @Override
  public void setTextChannel(TextChannel channel) {
    if (!new TicketNewChannelEvent(this, channel).callAndGet()) {
      this.channel = channel == null ? -1 : channel.getIdLong();
    }
  }

  @Override
  public void setStatus(@NonNull TicketStatus status) {
    TicketStatusUpdatedEvent event = new TicketStatusUpdatedEvent(this, status);
    boolean updated = event.callAndGet();
    if (!updated) {
      this.status = status;
    }
  }

  @Override
  public @NonNull StarfishTicket cache() {
    return (StarfishTicket) Ticket.super.cache();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    StarfishTicket that = (StarfishTicket) o;
    return this.id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", StarfishTicket.class.getSimpleName() + "[", "]")
        .add("id=" + this.id)
        .add("type=" + this.type)
        .add("users=" + this.users)
        .add("details=" + this.details)
        .add("channel=" + this.channel)
        .add("status=" + this.status)
        .toString();
  }
}
