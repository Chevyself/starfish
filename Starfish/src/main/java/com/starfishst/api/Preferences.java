package com.starfishst.api;

import com.starfishst.core.utils.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents certain changes and configurations for an object */
public interface Preferences {

  /**
   * Get the preference casted to certain class or null if the preference is not inside the map
   *
   * @param name the name of the preference
   * @param clazz the clazz to which the value of the preference will be casted to
   * @param <T> the type of the clazz to which the value of the preference will be casted to
   * @return the value of the preference or null if it does not have one
   */
  @Nullable
  default <T> T getPreference(@NotNull String name, @NotNull Class<T> clazz) {
    Object obj = this.getPreferences().get(name);
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
  @NotNull
  default <T> List<T> getListPreference(@NotNull String name) {
    List<T> list = new ArrayList<>();
    Class<List<T>> aClass = (Class<List<T>>) list.getClass();
    list.addAll(this.getPreferenceOr(name, aClass, new ArrayList<>()));
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
  @NotNull
  default <T> T getPreferenceOr(@NotNull String name, @NotNull Class<T> clazz, @NotNull T def) {
    return Validate.notNullOr(this.getPreference(name, clazz), def);
  }

  /**
   * The main preferences map to get the preferences from
   *
   * @return the preferences map
   */
  @NotNull
  Map<String, Object> getPreferences();
}
