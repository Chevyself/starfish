package com.starfishst.bot.handlers;

import com.starfishst.bot.Starfish;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/**
 * Handles JDA events and {@link com.starfishst.api.events.StarfishEvent}
 */
public interface StarfishEventHandler extends StarfishHandler {

    @Override
    default void register(@NotNull JDA api) {
        StarfishHandler.super.register(api);
        Starfish.getListenerManager().registerListeners(this);
    }

    @Override
    default void unregister(@NotNull JDA api) {
        StarfishHandler.super.unregister(api);
        Starfish.getListenerManager().unregister(this);
    }
}
