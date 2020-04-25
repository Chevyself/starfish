package com.starfishst.simple.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

/** Gson Provider and stuff */
public class GsonProvider {

  /** The map of adapters working in the gson instance */
  @NotNull private static final HashMap<Type, Object> adapters = new HashMap<>();
  /** The gson instance */
  @NotNull public static Gson GSON = build();

  /** Builds and sets the gson instance */
  public static void refresh() {
    GSON = build();
  }

  /**
   * Builds a gson instance using the map of adapters and pretty printing
   *
   * @return a gson instance
   */
  @NotNull
  public static Gson build() {
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    adapters.forEach((builder::registerTypeAdapter));
    return builder.create();
  }

  /**
   * Adds and adapter to the map
   *
   * @param type the type that the adapter serializes and deserializes
   * @param adapter the adapter
   */
  public static void addAdapter(@NotNull Type type, @NotNull Object adapter) {
    adapters.put(type, adapter);
  }

  /**
   * Saves an object to a file
   *
   * @param file the file to write
   * @param toWrite the object to write
   * @throws IOException in case file handling goes wrong
   */
  public static void save(@NotNull File file, @NotNull Object toWrite) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    fileWriter.write(GSON.toJson(toWrite));
    fileWriter.close();
  }
}
