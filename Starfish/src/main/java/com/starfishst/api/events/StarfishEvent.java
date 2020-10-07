package com.starfishst.api.events;

import com.starfishst.bot.Starfish;
import me.googas.commons.events.Event;

/** An event that can be called by the Starfish Bot */
public interface StarfishEvent extends Event {

  /** Calls the event from the {@link Starfish#getListenerManager()} */
  default void call() {
    Starfish.getListenerManager().call(this);
  }
}
