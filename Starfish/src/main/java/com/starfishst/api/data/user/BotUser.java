package com.starfishst.api.data.user;

import com.starfishst.api.Preferences;
import org.jetbrains.annotations.NotNull;

/**
 * An user that uses the Starfish Studios services
 */
public interface BotUser {

    /**
     * Get the id that is used to represent this used in discord
     * @return the id of the user given in a long
     */
    long getId();

    /**
     * Get the preferences for the user
     *
     * @return the preferences
     */
    @NotNull
    Preferences getPreferences();
}
