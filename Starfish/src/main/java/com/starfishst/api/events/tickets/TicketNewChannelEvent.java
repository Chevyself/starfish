package com.starfishst.api.events.tickets;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.events.StarfishCancellable;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a {@link com.starfishst.api.data.tickets.Ticket} has a new channel
 */
public class TicketNewChannelEvent extends TicketEvent implements StarfishCancellable {

    /**
     * The new channel of the ticket
     */
    @Nullable
    private final TextChannel channel;

    /**
     * Whether the event is cancelled
     */
    private boolean cancelled;

    /**
     * Create the event
     *
     * @param ticket the ticket involved in the event
     * @param channel the new channel of the ticket
     */
    public TicketNewChannelEvent(@NotNull Ticket ticket, @Nullable TextChannel channel) {
        super(ticket);
        this.channel = channel;
    }

    /**
     * Get the new channel set on the ticket
     *
     * @return the channel set on the ticket
     */
    @Nullable
    public TextChannel getChannel() {
        return channel;
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
