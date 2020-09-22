package com.starfishst.api.configuration;

import com.starfishst.api.Preferences;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.commands.ManagerOptions;
import com.starfishst.core.utils.time.Time;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * The configuration for {@link com.starfishst.bot.Starfish}
 */
public interface Configuration {

    /**
     * Get the time to unload tickets
     *
     * @return the time to unload tickets from cache
     */
    @NotNull
    Time toUnloadTickets();

    /**
     * Get the time to unload users
     *
     * @return the time to unload users from cache
     */
    @NotNull
    Time toUnloadUser();

    /**
     * Get the configuration for the mongo loader
     *
     * @return the configuration for mongo
     */
    @NotNull
    MongoConfiguration mongoConfiguration();

    /**
     * Set the total of tickets created
     *
     * @param total the total of tickets created
     */
    void setTotal(long total);

    /**
     * Get the total of tickets created
     * @return the total of tickets created
     */
    long getTotal();

    /**
     * Get the limit that freelancers have to send quotes
     *
     * @return the limit that freelancers have to send quotes
     */
    long getLimitOfQuotes();

    /**
     * Get the limit that a user has to have open tickets
     *
     * @return the open tickets limit
     */
    long getOpenLimit();

    /**
     * Get the configuration for commands
     *
     * @return the options for commands
     */
    @NotNull
    ManagerOptions getManagerOptions();

    /**
     * Get the preferences for starfish handlers
     *
     * @return the preferences for starfish handlers
     */
    @NotNull
    HashMap<String, ? extends Preferences> getHandlerPreferences();

}