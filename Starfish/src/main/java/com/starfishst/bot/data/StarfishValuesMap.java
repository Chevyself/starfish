package com.starfishst.bot.data;

import com.starfishst.api.ValuesMap;
import java.util.HashMap;
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
  public StarfishValuesMap(@NotNull HashMap<String, Object> preferences) {
    this.preferences = preferences;
  }

  @Override
  public @NotNull HashMap<String, Object> getMap() {
    return this.preferences;
  }
}
