package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishCancellable;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Called when a {@link BotUser} is removed from a ticket */
public class TicketRemoveUserEvent extends TicketEvent implements StarfishCancellable {

  @NonNull @Getter private final BotUser user;
  @Getter @Setter private boolean cancelled;

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   * @param user the user that is being removed
   */
  public TicketRemoveUserEvent(@NonNull Ticket ticket, @NonNull BotUser user) {
    super(ticket);
    this.user = user;
  }
}
