package com.starfishst.api.events.tickets;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.StarfishCancellable;
import org.jetbrains.annotations.NotNull;

/** Called when a {@link BotUser} is removed from a ticket */
public class TicketRemoveUserEvent extends TicketEvent implements StarfishCancellable {

  /** The user that is being removed */
  @NotNull private final BotUser user;

  /** Whether the event is cancelled */
  private boolean cancelled;

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   * @param user the user that is being removed
   */
  public TicketRemoveUserEvent(@NotNull Ticket ticket, @NotNull BotUser user) {
    super(ticket);
    this.user = user;
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
   * Get the user that is joining the ticket
   *
   * @return the user that is joining the ticket
   */
  @NotNull
  public BotUser getUser() {
    return user;
  }
}
