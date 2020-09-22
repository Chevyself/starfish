package com.starfishst.bot;

import com.starfishst.api.configuration.Configuration;
import com.starfishst.bot.configuration.StarfishConfiguration;
import com.starfishst.commands.CommandManager;
import com.starfishst.utils.events.ListenerManager;
import org.jetbrains.annotations.NotNull;

/**
 * The main class of the starfish bot
 */
public class Starfish {

    /**
     * The manager for listeners and events
     */
    @NotNull
    private static final ListenerManager listenerManager = new ListenerManager();

    /**
     * The configuration that the bot will be using
     */
    @NotNull
    private static final Configuration configuration = StarfishConfiguration.fallback();

    /**
     * The main method of the bot
     *
     * @param args the arguments include:
     *
     */
    public static void main(String[] args) {

    }

    /**
     * Get the listener manager that the bot is using
     *
     * @return the listener manager
     */
    @NotNull
    public static ListenerManager getListenerManager() {
        return listenerManager;
    }

    /**
     * Get the configuration that the bot is using
     *
     * @return the bot configuration
     */
    @NotNull
    public static Configuration getConfiguration() {
        return configuration;
    }
}
