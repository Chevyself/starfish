package com.starfishst.bot.handlers;

import com.starfishst.api.utility.ValuesMap;
import java.util.HashMap;
import lombok.NonNull;

/** The preferences for a starfish handler */
public class StarfishHandlerValuesMap implements ValuesMap {

  /** The map of the preferences */
  @NonNull private final HashMap<String, Object> preferences;

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
  public StarfishHandlerValuesMap(@NonNull HashMap<String, Object> preferences) {
    this.preferences = preferences;
  }

  @Override
  public @NonNull HashMap<String, Object> getMap() {
    return this.preferences;
  }
}
