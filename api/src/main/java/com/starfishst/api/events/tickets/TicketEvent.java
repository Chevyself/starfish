package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishEvent;
import com.starfishst.api.tickets.Ticket;
import lombok.Getter;
import lombok.NonNull;

/** An event related to a ticket */
public class TicketEvent implements StarfishEvent {

  @NonNull @Getter private final Ticket ticket;

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   */
  public TicketEvent(@NonNull Ticket ticket) {
    this.ticket = ticket;
  }
}
