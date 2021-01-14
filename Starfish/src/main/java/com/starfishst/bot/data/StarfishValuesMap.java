package com.starfishst.bot.data;

import com.starfishst.api.utility.ValuesMap;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.maps.Maps;

/** An implementation for {@link ValuesMap} used in {@link StarfishUser} */
public class StarfishValuesMap implements ValuesMap {

  @Getter @NonNull private final HashMap<String, Object> map;

  /**
   * Create the preferences
   *
   * @param map the map of preferences
   */
  public StarfishValuesMap(@NonNull Map<String, Object> map) {
    this.map = new HashMap<>(map);
  }

  public StarfishValuesMap(@NonNull String key, @NonNull Object value) {
    this(Maps.singleton(key, value));
  }

  /** Create the preferences */
  public StarfishValuesMap() {
    this.map = new HashMap<>();
  }
}
