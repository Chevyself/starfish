package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishCancellable;
import com.starfishst.api.tickets.Ticket;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Called when a detail is gong to be added to a ticket */
public class TicketAddDetailEvent extends TicketEvent implements StarfishCancellable {

  @NonNull @Getter private final String simple;
  @NonNull @Getter private final Object detail;
  @Getter @Setter private boolean cancelled;
  @NonNull @Getter @Setter private String reason = "No reason given";

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   * @param simple the simple of the detail that is being added
   * @param detail the value of the detail
   */
  public TicketAddDetailEvent(
      @NonNull Ticket ticket, @NonNull String simple, @NonNull Object detail) {
    super(ticket);
    this.simple = simple;
    this.detail = detail;
  }
}
