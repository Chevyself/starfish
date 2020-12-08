package com.starfishst.api.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.Validate;

/** This object represents certain changes and configurations for an object */
public interface ValuesMap {

  /**
   * Get the preference casted to certain class or null if the preference is not inside the map
   *
   * @param name the name of the preference
   * @param clazz the clazz to which the value of the preference will be casted to
   * @param <T> the type of the clazz to which the value of the preference will be casted to
   * @return the value of the preference or null if it does not have one
   */
  default <T> T getValue(@NonNull String name, @NonNull Class<T> clazz) {
    Object obj = this.getMap().get(name);
    if (obj != null) {
      return clazz.cast(obj);
    } else {
      return null;
    }
  }

  /**
   * Get a preference that happens to be a list
   *
   * @param name the name of the preference
   * @param <T> the type of the class
   * @return the list or empty if the preference does not exist
   */
  @SuppressWarnings("unchecked")
  @NonNull
  default <T> List<T> getListValue(@NonNull String name) {
    List<T> list = new ArrayList<>();
    Class<List<T>> aClass = (Class<List<T>>) list.getClass();
    list.addAll(this.getValueOr(name, aClass, new ArrayList<>()));
    return list;
  }

  /**
   * Get the preference casted to certain class or a default value
   *
   * @param name the name of the preference
   * @param clazz the clazz to which the value of the preference will be casted to
   * @param def the default value in case the preference is null
   * @param <T> the type of the clazz to which the value of the preference will be casted to
   * @return the value of the preference or null if it does not have one
   */
  @NonNull
  default <T> T getValueOr(@NonNull String name, @NonNull Class<T> clazz, @NonNull T def) {
    return Validate.notNullOr(this.getValue(name, clazz), def);
  }

  /**
   * Add a value to the map
   *
   * @param name the name of the value
   * @param value the value to add
   */
  default void addValue(@NonNull String name, @NonNull Object value) {
    this.getMap().put(name, value);
  }

  /**
   * Adds the map values to this map
   *
   * @param map the map to add the values from
   */
  default void addValues(@NonNull Map<String, Object> map) {
    map.forEach(this::addValue);
  }

  /**
   * Adds the map values to this map
   *
   * @param map the map to add the values from
   */
  default void addValues(@NonNull ValuesMap map) {
    this.addValues(map.getMap());
  }

  /**
   * Removes a value from the map
   *
   * @param key the key inside the map
   */
  default void removeValue(@NonNull String key) {
    this.getMap().remove(key);
  }

  /**
   * Convert the preferences into a string map. Maybe to be read by an user?
   *
   * @return the string map
   */
  @NonNull
  default Map<String, String> toStringMap() {
    HashMap<String, String> stringMap = new HashMap<>();
    this.getMap()
        .forEach(
            (key, value) -> {
              if (value instanceof List) {
                Class<?> clazz = Lots.getClazz((List<?>) value);
                if (clazz != null && Long.class.isAssignableFrom(clazz)) {
                  stringMap.put(key, Lots.pretty(Discord.getRolesAsMention(this.getListValue(key))));
                  return;
                }
              } else if (value instanceof Collection) {
                stringMap.put(key, Lots.pretty((Collection<?>) value));
                return;
              }
              stringMap.put(key, value.toString());
            });
    return stringMap;
  }

  /**
   * The main preferences map to get the preferences from
   *
   * @return the preferences map
   */
  @NonNull
  Map<String, Object> getMap();
}
