package com.starfishst.bot.data;

import com.starfishst.api.utility.ValuesMap;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

/** An implementation for {@link ValuesMap} used in {@link StarfishUser} */
public class StarfishValuesMap implements ValuesMap {

  /** The map of preferences */
  @NonNull private final HashMap<String, Object> preferences;

  /**
   * Create the preferences
   *
   * @param preferences the map of preferences
   */
  public StarfishValuesMap(@NonNull Map<String, Object> preferences) {
    this.preferences = new HashMap<>(preferences);
  }
  /** Create the preferences */
  public StarfishValuesMap() {
    this.preferences = new HashMap<>();
  }

  @Override
  public @NonNull HashMap<String, Object> getMap() {
    return this.preferences;
  }
}
