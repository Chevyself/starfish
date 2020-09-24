package com.starfishst.api.lang;

import com.starfishst.core.utils.Strings;
import com.starfishst.core.utils.maps.MapBuilder;
import java.io.File;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The file of localized messages. Used to get the messages for {@link Localizable} */
public interface LocaleFile extends Localizable {

  /** Saves the locale file */
  void save();

  /**
   * Get the raw string from the file
   *
   * @param path the path to the string
   * @return the string if the path leads to one else null
   */
  @Nullable
  String getRaw(@NotNull String path);

  /**
   * Get the string or the path to create it
   *
   * @param path the path to the string
   * @return the string if the path leads to one else the path
   */
  @NotNull
  default String get(@NotNull String path) {
    String raw = this.getRaw(path);
    return raw == null ? path : raw;
  }

  /**
   * Get the string and build it with placeholders. It will replace the placeholders that are inside
   * a "%" character
   *
   * @param path the path that leads to the string
   * @param placeholders the string to build the string
   * @return the built string
   */
  @NotNull
  default String get(@NotNull String path, @NotNull HashMap<String, String> placeholders) {
    return Strings.buildMessage(this.get(path), placeholders);
  }

  /**
   * Get the string and build it with placeholders using a builder. It will replace the placeholders
   * that are inside a "%" character
   *
   * @param path the path that leads to the string
   * @param placeholders the string to build the string
   * @return the built string
   */
  @NotNull
  default String get(@NotNull String path, @NotNull MapBuilder<String, String> placeholders) {
    return Strings.buildMessage(this.get(path), placeholders);
  }

  /**
   * Get the actual file that this is using
   *
   * @return the file that this is using
   */
  @NotNull
  File getFile();
}
