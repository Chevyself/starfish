package com.starfishst.bot.data;

import com.starfishst.api.ValuesMap;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link ValuesMap} used in {@link StarfishUser} */
public class StarfishValuesMap implements ValuesMap {

  /** The map of preferences */
  @NotNull private final HashMap<String, Object> preferences;

  /**
   * Create the preferences
   *
   * @param preferences the map of preferences
   */
  public StarfishValuesMap(@NotNull Map<String, Object> preferences) {
    this.preferences = new HashMap<>(preferences);
  }
  /**
   * Create the preferences
   */
  public StarfishValuesMap() {
    this.preferences = new HashMap<>();
  }

  @Override
  public @NotNull HashMap<String, Object> getMap() {
    return this.preferences;
  }
}
