package com.starfishst.bot.util;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/**
 * Many utils for maps
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Maps {

  /**
   * Converts a hash map that has long as keys to one that the keys are strings
   *
   * @param toConvert the hash map to convert
   * @param <O> the values type
   * @return the hash map with strings as keys
   */
  @NotNull
  public static <O> HashMap<String, O> getWithKeysAsStrings(@NotNull HashMap<Long, O> toConvert) {
    HashMap<String, O> map = new HashMap<>();
    toConvert.forEach(((aLong, o) -> map.put(String.valueOf(aLong), o)));
    return map;
  }

  /**
   * Converts a hash map that has strings as keys to one that the keys are longs
   *
   * @param toConvert the hash map to convert
   * @param <O> the values type
   * @return the hash map with longs as keys
   * @throws NumberFormatException in case that the one of the keys to convert doesn't match a long
   */
  @NotNull
  public static <O> HashMap<Long, O> getWithKeysAsLong(@NotNull HashMap<String, O> toConvert) {
    HashMap<Long, O> map = new HashMap<>();
    toConvert.forEach(((string, o) -> map.put(Long.valueOf(string), o)));
    return map;
  }
}
