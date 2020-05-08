package com.starfishst.simple.config;

import com.starfishst.simple.files.FileUtils;
import com.starfishst.simple.gson.GsonProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A configuration file made with json */
public class JsonConfiguration {

  /** The file that this configuration is using */
  @Nullable private transient File file;

  /**
   * Create a T instance
   *
   * @param file the file to get the instance
   * @param clazz the json configuration class
   * @param <T> the json configuration type
   * @return a new instance
   * @throws FileNotFoundException in case that the file provided doesn't exist
   */
  @NotNull
  public static <T extends JsonConfiguration> T getInstance(@NotNull File file, Class<T> clazz)
      throws FileNotFoundException {
    T config = GsonProvider.GSON.fromJson(FileUtils.getReader(file), clazz);
    config.setFile(file);
    return config;
  }

  /**
   * Create a T instance
   *
   * @param name the name of the file to get
   * @param clazz the json configuration class
   * @param <T> the json configuration type
   * @return a new instance
   * @throws FileNotFoundException in case that the file provided doesn't exist
   */
  @NotNull
  public static <T extends JsonConfiguration> T getInstance(@NotNull String name, Class<T> clazz)
      throws IOException {
    return getInstance(FileUtils.getFileOrResource(name), clazz);
  }

  /**
   * Save this to its config file
   *
   * @throws IOException in case that it could not be saved into the file
   */
  public void save() throws IOException {
    if (this.file != null) {
      GsonProvider.save(this.file, this);
    }
  }

  /**
   * Set the file that this configuration is using
   *
   * @param file the new file to use
   */
  public void setFile(@Nullable File file) {
    this.file = file;
  }
}
