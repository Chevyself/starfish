package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishEvent;
import com.starfishst.api.data.tickets.Ticket;
import org.jetbrains.annotations.NotNull;

/**
 * An event related to a ticket
 */
public class TicketEvent implements StarfishEvent {

    /**
     * The ticket involved in the event
     */
    @NotNull
    private final Ticket ticket;

    /**
     * Create the event
     *
     * @param ticket the ticket involved in the event
     */
    public TicketEvent(@NotNull Ticket ticket) {
        this.ticket = ticket;
    }

    /**
     * Get the ticket involved in the event
     *
     * @return the ticket that was involved in the event
     */
    @NotNull
    public Ticket getTicket() {
        return ticket;
    }
}
