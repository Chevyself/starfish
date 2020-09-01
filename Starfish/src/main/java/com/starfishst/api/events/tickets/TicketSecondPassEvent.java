package com.starfishst.api.events.tickets;

import com.starfishst.api.tickets.Ticket;
import com.starfishst.core.utils.time.Time;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a second passes to unload a ticket. When the
 * {@link #getTimeLeft()} reaches 0 {@link TicketUnloadedEvent} will be called
 */
public class TicketSecondPassEvent extends TicketEvent {
    /**
     * Create the event
     *
     * @param ticket the ticket involved in the event
     */
    public TicketSecondPassEvent(@NotNull Ticket ticket) {
        super(ticket);
    }

    /**
     * Get the time left until the ticket gets unloaded
     *
     * @return the time left until the ticket gets unloaded
     */
    @NotNull
    public Time getTimeLeft() {
        return this.getTicket().getTimeLeft();
    }
}
