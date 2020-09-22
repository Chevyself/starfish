package com.starfishst.api.events.user;

import com.starfishst.api.data.user.BotUser;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a bot user gets unloaded
 */
public class BotUserUnloadedEvent extends BotUserEvent {
    /**
     * Create the event
     *
     * @param user the user involved in the event
     */
    public BotUserUnloadedEvent(@NotNull BotUser user) {
        super(user);
    }
}
