package com.starfishst.api.events;

import com.starfishst.api.Starfish;
import me.googas.starbox.events.Event;

/** An event that can be called by the Starfish Bot */
public interface StarfishEvent extends Event {

  /** Calls the event from the {@link Starfish#getListenerManager()} */
  default void call() {
    Starfish.getListenerManager().call(this);
  }
}
