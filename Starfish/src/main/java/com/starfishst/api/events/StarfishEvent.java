package com.starfishst.api.events;


import com.starfishst.utils.events.Event;

/**
 * An event that can be called by the Starfish Bot
 */
public interface StarfishEvent extends Event {

    /**
     *
     */
    default void call() {
        // TODO this must be called using the bot
    }

}
