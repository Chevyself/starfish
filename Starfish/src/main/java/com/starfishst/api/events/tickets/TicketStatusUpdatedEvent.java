package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishCancellable;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link com.starfishst.api.data.tickets.Ticket} changes of status
 */
public class TicketStatusUpdatedEvent extends TicketEvent implements StarfishCancellable {

    /**
     * The new status of the ticket
     */
    @NotNull
    private final TicketStatus status;

    /**
     * Whether the event is cancelled
     */
    private boolean cancelled;

    /**
     * Create the event
     *
     * @param ticket the ticket involved in the event
     * @param status the new status of the ticket
     */
    public TicketStatusUpdatedEvent(@NotNull Ticket ticket, @NotNull TicketStatus status) {
        super(ticket);
        this.status = status;
    }

    /**
     * Get the new status of the ticket
     *
     * @return the new status of the ticket
     */
    @NotNull
    public TicketStatus getStatus() {
        return status;
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
