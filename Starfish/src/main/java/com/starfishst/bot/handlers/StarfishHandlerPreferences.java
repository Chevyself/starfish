package com.starfishst.bot.handlers;

import com.starfishst.api.Preferences;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** The preferences for a starfish handler */
public class StarfishHandlerPreferences implements Preferences {

  /** The map of the preferences */
  @NotNull private final HashMap<String, Object> preferences;

  /** Create the handler preferences. This constructor may only be used by Gson */
  @Deprecated
  public StarfishHandlerPreferences() {
    this.preferences = new HashMap<>();
  }

  /**
   * Create the handler preferences
   *
   * @param preferences the map with the preferences
   */
  public StarfishHandlerPreferences(@NotNull HashMap<String, Object> preferences) {
    this.preferences = preferences;
  }

  @Override
  public @NotNull HashMap<String, Object> getPreferences() {
    return this.preferences;
  }
}
