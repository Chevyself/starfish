package com.starfishst.api.events.tickets;

import com.starfishst.bot.tickets.StarfishTicket;
import org.jetbrains.annotations.NotNull;

/** Called when a ticket is loaded */
public class TicketLoadedEvent extends TicketEvent {
  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   */
  public TicketLoadedEvent(@NotNull StarfishTicket ticket) {
    super(ticket);
  }
}
