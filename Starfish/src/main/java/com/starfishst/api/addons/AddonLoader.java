package com.starfishst.api.addons;

import java.util.Collection;
import lombok.NonNull;

/** This object loads addons into the bot. It also can provide them */
public interface AddonLoader {

  /**
   * Load new addons
   *
   * @return the collection of the new addons loaded
   */
  @NonNull
  Collection<Addon> load();

  /**
   * Unload all the loaded addons
   *
   * @return the collection of the addons that were unloaded
   */
  @NonNull
  Collection<Addon> unload();

  /**
   * Unload the given addon
   *
   * @param addon the addon to unload
   * @return the same addon instance
   */
  @NonNull
  default Addon unload(@NonNull Addon addon) {
    try {
      addon.onDisable();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    return addon;
  }

  /** Reloads addons */
  default void reload() {
    this.unload();
    this.load();
  }

  /**
   * Get an addon by name
   *
   * @param name the name to match
   * @return the addon if the name matches
   */
  default Addon getAddon(@NonNull String name) {
    for (Addon addon : this.getLoaded()) {
      if (addon.getInformation().getName().equalsIgnoreCase(name)) return addon;
    }
    return null;
  }

  /**
   * Get the collection of loaded addons
   *
   * @return the collection of addons that have been loaded
   */
  @NonNull
  Collection<Addon> getLoaded();
}
