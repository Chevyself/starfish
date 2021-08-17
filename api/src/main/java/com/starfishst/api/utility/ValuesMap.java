package com.starfishst.api.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/** This object represents certain changes and configurations for an object */
public class ValuesMap {

  @NonNull @Getter private final Map<String, Object> map;

  public ValuesMap(@NonNull Map<String, Object> map) {
    this.map = map;
  }

  public ValuesMap() {
    this.map = new LinkedHashMap<>();
  }

  public ValuesMap(@NonNull String key, @NonNull Object value) {
    this();
    this.map.put(key, value);
  }

  /**
   * Get the preference casted to certain class or null if the preference is not inside the map
   *
   * @param name the name of the preference
   * @param clazz the clazz to which the value of the preference will be casted to
   * @param <T> the type of the clazz to which the value of the preference will be casted to
   * @return the value of the preference or null if it does not have one
   */
  public <T> T get(@NonNull String name, @NonNull Class<T> clazz) {
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
  public <T> List<T> getList(@NonNull String name) {
    List<T> list = new ArrayList<>();
    Class<List<T>> aClass = (Class<List<T>>) list.getClass();
    list.addAll(this.getOr(name, aClass, new ArrayList<>()));
    return list;
  }

  /**
   * Get the clazz of the objects inside a list. If the list is empty it will return null
   *
   * @param list the list to get the class from
   * @return the clazz of the list or empty if there's no object in the list
   */
  public static Class<?> getClazz(@NonNull List<?> list) {
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0).getClass();
  }

  /**
   * Add a value to the map
   *
   * @param name the name of the value
   * @param value the value to add
   */
  @NonNull
  public ValuesMap add(@NonNull String name, @NonNull Object value) {
    this.getMap().put(name, value);
    return this;
  }

  /**
   * Adds the map values to this map
   *
   * @param map the map to add the values from
   */
  @NonNull
  public ValuesMap add(@NonNull Map<String, Object> map) {
    map.forEach(this::add);
    return this;
  }

  /**
   * Adds the map values to this map
   *
   * @param map the map to add the values from
   */
  @NonNull
  public ValuesMap add(@NonNull ValuesMap map) {
    this.add(map.getMap());
    return this;
  }

  /**
   * Removes a value from the map
   *
   * @param key the key inside the map
   */
  public void remove(@NonNull String key) {
    this.getMap().remove(key);
  }

  @NonNull
  @Deprecated
  public static <O> String pretty(@NonNull Collection<O> collection, @Nullable String empty) {
    if (collection.isEmpty()) return empty == null ? "Empty" : empty;
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (O obj : collection) {
      if (first) {
        builder.append("- ").append(obj);
        first = false;
      } else {
        builder.append("\n- ").append(obj);
      }
    }
    return builder.toString();
  }

  /**
   * Gives a pretty string of a collection
   *
   * @param collection the collection to give as string
   * @param <O> the type of the collection
   * @return a string given by the collection
   */
  @NonNull
  @Deprecated
  public static <O> String pretty(@NonNull Collection<O> collection) {
    return collection.toString().replace("[", "").replace("]", "");
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
  public <T> T getOr(@NonNull String name, @NonNull Class<T> clazz, @NonNull T def) {
    T t = this.get(name, clazz);
    return t == null ? def : t;
  }

  /**
   * Convert the preferences into a string map. Maybe to be read by an user?
   *
   * @return the string map
   */
  @NonNull
  public Map<String, String> toStringMap() {
    HashMap<String, String> stringMap = new HashMap<>();
    this.getMap()
        .forEach(
            (key, value) -> {
              if (value instanceof List) {
                Class<?> clazz = ValuesMap.getClazz((List<?>) value);
                if (clazz != null && Long.class.isAssignableFrom(clazz)) {
                  stringMap.put(
                      key, ValuesMap.pretty(Discord.getRolesAsMention(this.getList(key))));
                  return;
                }
              } else if (value instanceof Collection) {
                stringMap.put(key, ValuesMap.pretty((Collection<?>) value));
                return;
              }
              stringMap.put(key, value.toString());
            });
    return stringMap;
  }
}
