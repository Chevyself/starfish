package com.starfishst.bot.tickets;

import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.loader.TicketManager;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.events.tickets.TicketPreCreationEvent;
import com.starfishst.bot.users.StarfishUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation for {@link TicketManager}
 */
public class StarfishTicketManager implements TicketManager {

    /**
     * The data loader
     */
    @NotNull
    private DataLoader loader;

    /**
     * The configuration to keep track of the total of tickets and other stuff
     */
    @NotNull
    private Configuration configuration;

    /**
     * Create the ticket manager
     *
     * @param loader the data loader to get the tickets
     * @param configuration the configuration that the bot is using
     */
    public StarfishTicketManager(@NotNull DataLoader loader, @NotNull Configuration configuration) {
        this.loader = loader;
        this.configuration = configuration;
    }

    @Override
    public @NotNull Ticket createTicket(@NotNull TicketType type, @NotNull StarfishUser user, @Nullable Ticket parent) {
        if (new TicketPreCreationEvent(this, type, user, parent).callAndGet()) {
            // TODO when cancelled
        } else {
            return new StarfishTicket(this.configuration.toUnloadTickets(), this.getNewId(parent), type, TicketStatus.CREATING, user, );
        }
    }

    @Override
    public @NotNull DataLoader getDataLoader() {
        return this.loader;
    }

    @Override
    public long getTotal() {
        return this.configuration.getTotal();
    }

    @Override
    public void setTotal(long total) {
        this.configuration.setTotal(total);
    }

}
