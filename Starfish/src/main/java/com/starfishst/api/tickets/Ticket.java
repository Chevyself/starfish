package com.starfishst.api.tickets;

import com.starfishst.api.events.tickets.TicketLoadedEvent;
import com.starfishst.api.events.tickets.TicketSecondPassEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.catalina.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Represents a Ticket. TODO This documentation has to be a little bit more done.
 */
public class Ticket extends Catchable {

    /**
     * The id of the ticket
     */
    private final long id;

    /**
     * The type of ticket
     */
    @NotNull
    private final TicketType type;
    /**
     * The map of users inside the ticket
     */
    @NotNull
    private final HashMap<String, User> users;
    /**
     * The channel where the ticket is happening
     */
    @Nullable
    private final TextChannel channel;
    /**
     * The status of the ticket
     */
    @NotNull
    private TicketStatus status;

    /**
     * Create the ticket. This method will call {@link com.starfishst.api.events.tickets.TicketLoadedEvent}
     *
     * @param toRemove the time for the ticket to unload
     * @param id the id of the ticket
     * @param type the type of the ticket
     * @param status the status of the ticket
     * @param users the status that are inside the ticket
     * @param channel the channel where the ticket is occurring
     */
    public Ticket(@NotNull Time toRemove, long id, @NotNull TicketType type, @NotNull TicketStatus status, @NotNull HashMap<String, User> users, @Nullable TextChannel channel) {
        super(toRemove);
        this.id = id;
        this.type = type;
        this.status = status;
        this.users = users;
        this.channel = channel;
        new TicketLoadedEvent(this).call();
    }

    @Override
    public void onSecondsPassed() {
        new TicketSecondPassEvent(this).call();
    }

    @Override
    public void onRemove() {
        new TicketUnloadedEvent(this).call();
    }

}
