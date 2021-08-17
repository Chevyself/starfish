package com.starfishst.api.lang;

import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import me.googas.io.StarboxFile;
import me.googas.starbox.Strings;
import me.googas.starbox.builders.MapBuilder;

/** The file of localized messages. Used to get the messages for {@link Localizable} */
public interface LocaleFile extends Localizable {

  /**
   * Saves the locale file
   *
   * @return this same instance
   */
  @NonNull
  LocaleFile save();

  /**
   * Get the raw string from the file
   *
   * @param path the path to the string
   * @return a {@link java.util.Optional} holding the nullable string
   */
  @NonNull
  Optional<String> getRaw(@NonNull String path);

  /**
   * Get the string or the path to create it
   *
   * @param path the path to the string
   * @return the string if the path leads to one else the path
   */
  @NonNull
  default String get(@NonNull String path) {
    return this.getRaw(path).orElseGet(() -> path);
  }

  /**
   * Get the string and build it with placeholders. It will replace the placeholders that are inside
   * a "%" character
   *
   * @param path the path that leads to the string
   * @param placeholders the string to build the string
   * @return the built string
   */
  @NonNull
  default String get(@NonNull String path, @NonNull Map<String, String> placeholders) {
    return Strings.format(this.get(path), placeholders);
  }

  /**
   * Get the string and build it with placeholders using a builder. It will replace the placeholders
   * that are inside a "%" character
   *
   * @param path the path that leads to the string
   * @param placeholders the string to build the string
   * @return the built string
   */
  @NonNull
  default String get(@NonNull String path, @NonNull MapBuilder<String, String> placeholders) {
    return Strings.format(this.get(path), placeholders);
  }

  /**
   * Get the actual file that this is using
   *
   * @return the file that this is using
   */
  @NonNull
  StarboxFile getFile();

  /**
   * Get the unicode that differentiates this language
   *
   * @return the unicode to differentiate this language
   */
  default @NonNull String getUnicode() {
    return this.getRaw("unicode")
        .orElseThrow(() -> new NullPointerException(this + " has a null unicode property"));
  }
}
