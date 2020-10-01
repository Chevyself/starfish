package com.starfishst.api.events.tickets;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.events.StarfishCancellable;
import org.jetbrains.annotations.NotNull;

/** Called when a detail is gong to be added to a ticket */
public class TicketAddDetailEvent extends TicketEvent implements StarfishCancellable {

  /** The simple of the detail that is being added */
  @NotNull private final String simple;

  /** The value of the detail */
  @NotNull private final Object detail;

  /** Whether the event is cancelled */
  private boolean cancelled;

  /** The reason to why it was cancelled */
  @NotNull private String reason = "No reason given";

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   * @param simple the simple of the detail that is being added
   * @param detail the value of the detail
   */
  public TicketAddDetailEvent(
      @NotNull Ticket ticket, @NotNull String simple, @NotNull Object detail) {
    super(ticket);
    this.simple = simple;
    this.detail = detail;
  }

  /**
   * Get the key of the value being added
   *
   * @return the simple of the value
   */
  @NotNull
  public String getSimple() {
    return simple;
  }

  /**
   * Get the value of the detail that was added
   *
   * @return the value
   */
  @NotNull
  public Object getDetail() {
    return detail;
  }

  /**
   * Get the reason to why the event was cancelled
   *
   * @return the reason
   */
  @NotNull
  public String getReason() {
    return reason;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean bol) {
    this.cancelled = bol;
  }
}
