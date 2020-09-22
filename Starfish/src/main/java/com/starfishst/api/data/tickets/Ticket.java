package com.starfishst.api.data.tickets;

import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.core.utils.time.Time;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Represents a ticket made by a customer
 */
public interface Ticket {

    /**
     * Adds an user to the ticket
     *
     * @param user the user that is going to be added
     * @param role the role of the user
     */
    default void addUser(@NotNull BotUser user, @NotNull String role) {
        if (!new TicketAddUserEvent(this, user, role).callAndGet()) {
            this.getUsers().put(user, role);
        }
    }

    /**
     * Removes an user from the ticket
     *
     * @param user the user to remove from the ticket
     */
    default void removeUser(@NotNull BotUser user) {
        if (!new TicketRemoveUserEvent(this, user).callAndGet()) {
            this.getUsers().remove(user);
        }
    }

    /**
     * Set the text channel where the ticket is running. This must call
     * {@link com.starfishst.api.events.tickets.TicketNewChannelEvent}
     *
     * @param channel the new text channel where the ticket will run
     */
    void setTextChannel(@Nullable TextChannel channel);

    /**
     * Set the status of the ticket. This must call
     * {@link com.starfishst.api.events.tickets.TicketStatusUpdatedEvent}
     *
     * @param status the new status of the ticket
     */
    void setTicketStatus(@NotNull TicketStatus status);

    /**
     * Get the id to identify the ticket
     *
     * @return the id to identify the ticket
     */
    long getId();

    /**
     * Get the type of ticket
     *
     * @return the type of ticket
     */
    @NotNull
    TicketType getTicketType();

    /**
     * Get the users inside the ticket. The map shows the user and
     * its role in the ticket: 'user' or 'freelancer'
     *
     * @return a map of users inside the ticket
     */
    @NotNull HashMap<BotUser, String> getUsers();

    /**
     * Get the details of the ticket
     *
     * @return the details of the ticket
     */
    @NotNull
    TicketDetails getDetails();

    /**
     * Get the text channel where the ticket is running
     *
     * @return the text channel
     */
    @Nullable TextChannel getTextChannel();

    /**
     * Get the status in which the ticket is currently in
     *
     * @return the ticket status
     */
    @NotNull
    TicketStatus getTicketStatus();

    /**
     * Get the time left until the ticket is unloaded
     *
     * @return  the time left until the ticket is unloaded
     */
    @NotNull
    Time getTimeLeft();

}
