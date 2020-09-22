package com.starfishst.bot.oldconfig.language;

import com.starfishst.bot.exception.LanguageInitException;
import com.starfishst.core.utils.Strings;
import com.starfishst.simple.config.PropertiesConfiguration;
import java.io.IOException;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Class used for static methods related to the configuration file 'lang.properties' */
public class Lang {

  /** Constant values to be used across the bot */
  @NotNull public static final String GUILD_NOT_SET = "Guild has not been set";
  /** The instance of properties configuration */
  @NotNull private static final PropertiesConfiguration propertiesConfiguration;
  /** The messages provider */
  @NotNull private static final LangMessagesProvider provider = new LangMessagesProvider();

  static {
    try {
      System.out.println("Initializing language files");
      propertiesConfiguration = new PropertiesConfiguration("lang.properties", true);
    } catch (IOException e) {
      throw new LanguageInitException(e.getMessage());
    }
  }

  /**
   * Gets the raw message out of the configuration.
   *
   * @param key the key to get the message from
   * @return the message or 'null' if the provided key does not exist
   */
  @Nullable
  public static String getRaw(@NotNull String key) {
    return propertiesConfiguration.getProperties().getProperty(key);
  }

  /**
   * Gets the raw message out of the configuration.
   *
   * @param key the key to get the message from
   * @return the message or "Null" if the provided key does not exist
   */
  @NotNull
  public static String get(@NotNull String key) {
    String raw = getRaw(key);
    return raw == null ? key : raw;
  }

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
   * @return the built message or "Null" if the provided key does not exist
   */
  @NotNull
  public static String get(@NotNull String key, @NotNull HashMap<String, String> placeHolders) {
    String raw = getRaw(key);
    return raw == null ? key : Strings.buildMessage(raw, placeHolders);
  }

  /**
   * Sets a message in the file
   *
   * @param key the key to set the message
   * @param message the message
   */
  public static void set(@NotNull String key, @NotNull String message) {
    propertiesConfiguration.getProperties().setProperty(key, message);
  }

  /**
   * Saves the configuration
   *
   * @throws IOException in case anything related to files goes wrong
   */
  public static void save() throws IOException {
    propertiesConfiguration.save();
  }

  /**
   * Static usage of the provided
   *
   * @return the messages provider
   */
  @NotNull
  public static LangMessagesProvider getProvider() {
    return provider;
  }
}
