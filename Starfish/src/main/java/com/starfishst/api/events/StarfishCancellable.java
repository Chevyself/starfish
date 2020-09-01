package com.starfishst.api.events;

/**
 * An starfish event that can be cancelled.
 */
public interface StarfishCancellable {

    /**
     * Call the event and get if it was cancelled. This will be called
     * in the Starfish Bot
     *
     * @return true if the event was cancelled
     */
    default boolean callAndGet() {
        // TODO
        return false;
    }

}
