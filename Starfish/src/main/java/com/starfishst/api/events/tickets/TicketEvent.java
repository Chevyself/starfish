package com.starfishst.api.events.tickets;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.events.StarfishEvent;
import lombok.NonNull;

/** An event related to a ticket */
public class TicketEvent implements StarfishEvent {

  /** The ticket involved in the event */
  @NonNull private final Ticket ticket;

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   */
  public TicketEvent(@NonNull Ticket ticket) {
    this.ticket = ticket;
  }

  /**
   * Get the ticket involved in the event
   *
   * @return the ticket that was involved in the event
   */
  @NonNull
  public Ticket getTicket() {
    return ticket;
  }
}
