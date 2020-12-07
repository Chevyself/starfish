package com.starfishst.api.events.tickets;

import com.starfishst.bot.data.StarfishTicket;
import lombok.NonNull;

/** Called when an event gets unloaded */
public class TicketUnloadedEvent extends TicketEvent {
  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   */
  public TicketUnloadedEvent(@NonNull StarfishTicket ticket) {
    super(ticket);
  }
}
