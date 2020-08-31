package com.starfishst.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** Simple utils for lists and arrays */
public class Lots {

  /**
   * Get an array of objects as a list
   *
   * @param objects the array of objects
   * @param <O> the type of objects
   * @return the objects as a list
   */
  @SafeVarargs
  @NotNull
  public static <O> List<O> list(O... objects) {
    return new ArrayList<>(Arrays.asList(objects));
  }
}
