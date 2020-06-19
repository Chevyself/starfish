package com.starfishst.bot.config.language;

import java.io.IOException;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A message provider for the bot */
public interface MessagesProvider extends com.starfishst.commands.messages.MessagesProvider {

  /**
   * Gets the raw message out of the configuration.
   *
   * @param key the key to get the message from
   * @return the message or 'null' if the provided key does not exist
   */
  @Nullable
  String getRaw(@NotNull String key);

  /**
   * Gets the raw message out of the configuration.
   *
   * @param key the key to get the message from
   * @return the message or the key if the provided key does not exist
   */
  @NotNull
  String get(@NotNull String key);

  /**
   * Builds a message out of the configuration.
   *
   * <p>The way it builds the message is with the format: "string %placeholder%" Placeholders are
   * replaced with some kind of value given by the program:
   *
   * <p>If a developer tells you that you can use the placeholder "test" in your string you can use
   * it like this:
   *
   * <p>"This is a generic string also it has a place holder %test%"
   *
   * <p>It will be replaced in runtime
   *
   * @param key the key to get the message from
   * @param placeHolders the placeholders to build the message
   * @return the built message or the provided key if the provided key value does not exist
   */
  @NotNull
  String get(@NotNull String key, @NotNull HashMap<String, String> placeHolders);

  /**
   * Sets a message in the file
   *
   * @param key the key to set the message
   * @param message the message
   */
  void set(@NotNull String key, @NotNull String message);

  /**
   * Saves the configuration
   *
   * @throws IOException in case anything related to files goes wrong
   */
  void save() throws IOException;
}
