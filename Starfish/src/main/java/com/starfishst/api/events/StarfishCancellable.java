package com.starfishst.api.events;

import com.starfishst.bot.Starfish;
import me.googas.commons.events.Cancellable;

/** An starfish event that can be cancelled. */
public interface StarfishCancellable extends Cancellable {

  /**
   * Call the event and get if it was cancelled. This will be called in the Starfish Bot
   *
   * @return true if the event was cancelled
   */
  default boolean callAndGet() {
    return Starfish.getListenerManager().call(this);
  }

  /**
   * Get whether the event is cancelled
   *
   * @return true if the event is cancelled
   */
  boolean isCancelled();

  /**
   * Set whether the event must be cancelled
   *
   * @param bol the new value whether the event must be cancelled
   */
  void setCancelled(boolean bol);
}
