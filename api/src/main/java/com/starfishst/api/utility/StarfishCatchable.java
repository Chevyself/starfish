package com.starfishst.api.utility;

import com.starfishst.api.Starfish;
import lombok.NonNull;
import me.googas.net.cache.Catchable;

public interface StarfishCatchable extends Catchable {

  /**
   * Add to the Starfish cache
   *
   * @return the same object instance
   */
  @NonNull
  default StarfishCatchable cache() {
    Starfish.getCache().add(this);
    return this;
  }

  /**
   * Removes this object from the starfish cache and calling its remove method
   *
   * @throws Throwable in case something goes wrong while unloading the object
   */
  default void unload() {
    this.unload(true);
  }

  /**
   * Removes this object from the starfish cache and whether to call its on remove method
   *
   * @throws Throwable in case something goes wrong while unloading the object
   */
  default void unload(boolean onRemove) {
    if (onRemove) {
      try {
        this.onRemove();
      } catch (Throwable throwable) {
        Starfish.getFallback()
            .process(throwable, "There's been an error while unloading a catchable");
      }
    }
    Starfish.getCache().remove(this);
  }
}
