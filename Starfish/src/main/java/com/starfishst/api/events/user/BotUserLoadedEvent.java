package com.starfishst.api.events.user;

import com.starfishst.api.data.user.BotUser;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the bot user is loaded
 */
public class BotUserLoadedEvent extends BotUserEvent {

    /**
     * Create the event
     *
     * @param user the user involved in the event
     */
    public BotUserLoadedEvent(@NotNull BotUser user) {
        super(user);
    }
}
