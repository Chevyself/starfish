package com.starfishst.api.events.tickets;

import com.starfishst.api.tickets.Ticket;
import lombok.NonNull;

/** Called when an event gets unloaded */
@Deprecated
public class TicketUnloadedEvent extends TicketEvent {
  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   */
  public TicketUnloadedEvent(@NonNull Ticket ticket) {
    super(ticket);
  }
}
