package com.starfishst.api.events.tickets;

import com.starfishst.api.tickets.Ticket;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an event gets unloaded
 */
public class TicketUnloadedEvent extends TicketEvent {
    /**
     * Create the event
     *
     * @param ticket the ticket involved in the event
     */
    public TicketUnloadedEvent(@NotNull Ticket ticket) {
        super(ticket);
    }
}
