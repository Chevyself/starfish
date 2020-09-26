package com.starfishst.bot.data;

import com.starfishst.api.Preferences;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link Preferences} used in {@link StarfishUser} */
public class StarfishPreferences implements Preferences {

  /** The map of preferences */
  @NotNull private final HashMap<String, Object> preferences;

  /**
   * Create the preferences
   *
   * @param preferences the map of preferences
   */
  public StarfishPreferences(@NotNull HashMap<String, Object> preferences) {
    this.preferences = preferences;
  }

  @Override
  public @NotNull HashMap<String, Object> getPreferences() {
    return this.preferences;
  }
}
