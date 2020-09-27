package com.starfishst.api.events.tickets;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.StarfishCancellable;
import org.jetbrains.annotations.NotNull;

/** Called when a {@link BotUser} is added to a ticket */
public class TicketAddUserEvent extends TicketEvent implements StarfishCancellable {

  /** The user that is being added */
  @NotNull private final BotUser user;

  /** The role that the user will take in the ticket */
  @NotNull private final String role;

  /** Whether the event is cancelled */
  private boolean cancelled;

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   * @param user the user that will be added
   * @param role the role that the user will take in the ticket
   */
  public TicketAddUserEvent(@NotNull Ticket ticket, @NotNull BotUser user, @NotNull String role) {
    super(ticket);
    this.user = user;
    this.role = role;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean bol) {
    this.cancelled = bol;
  }

  /**
   * Get the user that is being added
   *
   * @return the user that is being added
   */
  @NotNull
  public BotUser getUser() {
    return user;
  }

  /**
   * Get the role to which the user is being added
   *
   * @return the role to which the user is being added
   */
  @NotNull
  public String getRole() {
    return role;
  }
}
