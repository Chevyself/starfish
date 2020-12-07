package com.starfishst.api.lang;

import java.io.File;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.Strings;
import me.googas.commons.Validate;
import me.googas.commons.maps.MapBuilder;

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
  String getRaw(@NonNull String path);

  /**
   * Get the string or the path to create it
   *
   * @param path the path to the string
   * @return the string if the path leads to one else the path
   */
  @NonNull
  default String get(@NonNull String path) {
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
  @NonNull
  default String get(@NonNull String path, @NonNull Map<String, String> placeholders) {
    return Strings.build(this.get(path), placeholders);
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
    return Strings.build(this.get(path), placeholders);
  }

  /**
   * Get the actual file that this is using
   *
   * @return the file that this is using
   */
  @NonNull
  File getFile();

  /**
   * Get the unicode that differentiates this language
   *
   * @return the unicode to differentiate this language
   */
  default @NonNull String getUnicode() {
    return Validate.notNull(this.getRaw("unicode"), this + " has a null unicode property");
  }
}
