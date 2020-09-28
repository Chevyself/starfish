package com.starfishst.bot.handlers;

import com.starfishst.api.ValuesMap;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** The preferences for a starfish handler */
public class StarfishHandlerValuesMap implements ValuesMap {

  /** The map of the preferences */
  @NotNull private final HashMap<String, Object> preferences;

  /** Create the handler preferences. This constructor may only be used by Gson */
  @Deprecated
  public StarfishHandlerValuesMap() {
    this.preferences = new HashMap<>();
  }

  /**
   * Create the handler preferences
   *
   * @param preferences the map with the preferences
   */
  public StarfishHandlerValuesMap(@NotNull HashMap<String, Object> preferences) {
    this.preferences = preferences;
  }

  @Override
  public @NotNull HashMap<String, Object> getMap() {
    return this.preferences;
  }
}
