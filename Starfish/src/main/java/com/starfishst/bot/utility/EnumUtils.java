package com.starfishst.bot.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;

/** Static utilities for enums */
public class EnumUtils {

  @NonNull
  public static <T extends Enum<?>> List<String> getNames(Collection<T> enums) {
    ArrayList<String> names = new ArrayList<>();
    if (enums.isEmpty()) return names;
    for (T anEnum : enums) {
      names.add(anEnum.name());
    }
    return names;
  }
}
