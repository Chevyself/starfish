package com.starfishst.api.data.loader;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Loads and saves data
 */
public interface DataLoader {

    /**
     * Get a ticket by its id
     *
     * @param id the id of the ticket
     * @return the returned ticket
     */
    @Nullable
    Ticket getTicket(long id);

    /**
     * Get an starfish user by its id
     *
     * @param id the id of the user
     * @return the starfish user
     */
    @NotNull
    BotUser getStarfishUser(long id);

}
