package com.starfishst.simple.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** Helps with gson */
public class GsonProvider {

  @NotNull private static final HashMap<Type, Object> adapters = new HashMap<>();
  @NotNull public static Gson GSON = build();

  public static void refresh() {
    GSON = build();
  }

  @NotNull
  public static Gson build() {
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    adapters.forEach((builder::registerTypeAdapter));
    return builder.create();
  }

  public static void addAdapter(@NotNull Type type, @NotNull Object adapter) {
    adapters.put(type, adapter);
  }

  public static void save(@NotNull File file, @NotNull Object toWrite) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    fileWriter.write(GSON.toJson(toWrite));
    fileWriter.close();
  }
}
