package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishCancellable;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Called when a {@link Ticket} changes of status */
public class TicketStatusUpdatedEvent extends TicketEvent implements StarfishCancellable {

  @NonNull @Getter private final TicketStatus status;
  @Getter @Setter private boolean cancelled;

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   * @param status the new status of the ticket
   */
  public TicketStatusUpdatedEvent(@NonNull Ticket ticket, @NonNull TicketStatus status) {
    super(ticket);
    this.status = status;
  }
}
