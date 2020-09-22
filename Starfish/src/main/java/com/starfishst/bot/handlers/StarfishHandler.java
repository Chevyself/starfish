package com.starfishst.bot.handlers;

import com.starfishst.api.Preferences;
import com.starfishst.bot.Starfish;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Handles JDA events
 */
public interface StarfishHandler {

    /**
     * Register the handler
     *
     * @param api the api to register the handler to
     */
    default void register(@NotNull JDA api) {
        api.addEventListener(this);
    }

    /**
     * Unregister the handler
     *
     * @param api the api to unregister the handler from
     */
    default void unregister(@NotNull JDA api) {
        this.onUnload();
        api.removeEventListener(this);
    }

    /**
     * Called on {@link #unregister(JDA)}
     */
    void onUnload();

    /**
     * Get the name of this handler
     *
     * @return the name of the handler
     */
    String getName();

    /**
     * Get the preferences for the handler
     *
     * @return the preferences
     */
    @NotNull
    default Preferences getPreferences() {
        Preferences preferences = Starfish.getConfiguration().getHandlerPreferences().get(this.getName());
        if (preferences != null) {
            return preferences;
        } else {
            return new StarfishHandlerPreferences(new HashMap<>());
        }
    }
}
