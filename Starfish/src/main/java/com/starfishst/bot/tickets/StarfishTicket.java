package com.starfishst.bot.tickets;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketDetails;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketLoadedEvent;
import com.starfishst.api.events.tickets.TicketNewChannelEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.events.tickets.TicketSecondPassEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.bot.Starfish;
import com.starfishst.core.utils.cache.Catchable;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a StarfishTicket. TODO This documentation has to be a little bit more done. */
public class StarfishTicket extends Catchable implements Ticket {

  /** The id of the ticket */
  private final long id;

  /** The type of ticket */
  @NotNull private final TicketType type;
  /** The map of users inside the ticket */
  @NotNull private final HashMap<BotUser, String> users;

  /** The details of the ticket */
  @NotNull private final StarfishTicketDetails details;

  /** The channel where the ticket is happening */
  @Nullable private TextChannel channel;
  /** The status of the ticket */
  @NotNull private TicketStatus status;

  /**
   * Create the ticket. This method will call {@link
   * com.starfishst.api.events.tickets.TicketLoadedEvent}
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
      @NotNull TicketType type,
      @NotNull StarfishTicketDetails details,
      @NotNull TicketStatus status,
      @NotNull HashMap<BotUser, String> users,
      @Nullable TextChannel channel) {
    super(Starfish.getConfiguration().toUnloadTickets());
    this.id = id;
    this.type = type;
    this.details = details;
    this.status = status;
    this.users = users;
    this.channel = channel;
    new TicketLoadedEvent(this).call();
  }

  @Override
  public void onSecondsPassed() {
    new TicketSecondPassEvent(this).call();
  }

  @Override
  public void onRemove() {
    new TicketUnloadedEvent(this).call();
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NotNull TicketType getTicketType() {
    return this.type;
  }

  @Override
  public @NotNull HashMap<BotUser, String> getUsers() {
    return this.users;
  }

  @Override
  public @NotNull TicketDetails getDetails() {
    return this.details;
  }

  @Override
  public @Nullable TextChannel getTextChannel() {
    return this.channel;
  }

  @Override
  public @NotNull TicketStatus getTicketStatus() {
    return this.status;
  }

  @Override
  public void addUser(@NotNull BotUser user, @NotNull String role) {
    if (!new TicketAddUserEvent(this, user, role).callAndGet()) {
      this.users.put(user, role);
    }
  }

  @Override
  public void removeUser(@NotNull BotUser user) {
    if (!new TicketRemoveUserEvent(this, user).callAndGet()) {
      this.users.remove(user);
    }
  }

  @Override
  public void setTextChannel(@Nullable TextChannel channel) {
    if (!new TicketNewChannelEvent(this, channel).callAndGet()) {
      this.channel = channel;
    }
  }

  @Override
  public void setTicketStatus(@NotNull TicketStatus status) {
    if (!new TicketStatusUpdatedEvent(this, status).callAndGet()) {
      this.status = status;
    }
  }

  @NotNull
  @Override
  public StarfishTicket refresh() {
    return (StarfishTicket) super.refresh();
  }
}
