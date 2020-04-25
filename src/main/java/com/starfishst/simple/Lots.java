package com.starfishst.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Lots {

  @SafeVarargs
  @NotNull
  public static <O> List<O> list(O... objects) {
    return new ArrayList<>(Arrays.asList(objects));
  }
}
