package com.starfishst.ethot.config.language;

import com.starfishst.core.utils.Strings;
import com.starfishst.ethot.exception.LanguageInitException;
import com.starfishst.simple.config.PropertiesConfiguration;
import java.io.IOException;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class used for static methods related to the configuration file 'lang.properties'
 *
 * <p>For the placeholders in the messages use %placeholder%
 *
 * <ul>
 *   <li>'LOG_FORMAT' This is used to change the format of the message in logs.
 *       <p>This includes the placeholders:
 *       <ul>
 *         <li>%level% the level of the log message
 *         <li>%day% the day that the message was sent
 *         <li>%month% the month that the message was sent
 *         <li>%year% the year that the message was sent
 *         <li>%hour% the hour that the message was sent
 *         <li>%minute% the minute that the message was sent
 *         <li>%message% the message sent
 *         <li>%stack% in case that the message is an error an stacktrace will be generated else it
 *             will be empty
 *       </ul>
 *   <li>'COMMAND_NOT_FOUND' upon command execution if the command is not found this message will be
 *       sent This includes the placeholders:
 *       <ul>
 *         <li>%command% which is the input send and not found
 *       </ul>
 *   <li>'NO_PERMISSION' send if the user does not have permission to use the command
 *   <li>'GUILD_ONLY' send if the user cannot use a command outside the guild
 *   <li>'MISSING_ARGUMENT' in case that the command executed is missing arguments
 *       <p>This includes the placeholders:
 *       <ul>
 *         <li>%name% the name of the argument
 *         <li>%description% the description of the argument
 *         <li>%position% the position where the argument was placed
 *       </ul>
 *   <li>'FOOTER' the footer used in embeds
 *   <li>'TITLE_ERROR' the title when a message regarding an error occurs
 *   <li>'TITLE_UNKNOWN' the title when a message regarding an unknown error occurs
 *   <li>'TITLE_USAGE' the title when a message regarding an usage error occurs
 *   <li>'TITLE_GENERIC' the title when a message regarding something generic occurs
 *   <li>'TITLE_PERMISSION' the title when a message regarding an permission error occurs
 *   <li>'THUMBNAIL' embeds can have a thumbnail. To set a thumbnail you must set the url to an
 *       image here to use the picture of the bot set it to 'bot' or leave it empty, to not use
 *       anything set it to 'none'
 *   <li>'RESPONSE' in case that in 'config.json' you set 'embedMessages' to false this is the
 *       message that will be send
 *       <p>This includes the placeholders:
 *       <ul>
 *         <li>%type% which is the type of result (error, unknown, usage, generic, permission)
 *         <li>%message% the message sent
 *       </ul>
 *       This part includes categories name: If you want some tickets to be in the same category
 *       name the categories the same.
 *       <p>If you don't create the categories manually and let the bot do the work it will also use
 *       the value as the name of the category but you can change it later from discord
 *   <li>'ORDERS_CATEGORY_NAME' the name of the category for orders
 *   <li>'APPLIES_CATEGORY_NAME' the name of the category for applies
 *   <li>'SUPPORT_CATEGORY_NAME' the name of the category for support
 *   <li>'TICKET_CREATOR_CATEGORY_NAME' the name of the category for creator tickets
 *   <li>'QUOTE_CATEGORY_NAME' the name of the category for quotes
 *   <li>'ORDERS_CHANNEL_NAME' the name of the announce channel for orders
 *   <li>'APPLIES_CHANNEL_NAME' the name of the announce channel for applies
 *   <li>'SUPPORT_CHANNEL_NAME' the name of the announce channel for support tickets
 *   <li>'QUOTE_CHANNEL_NAME' the name of the announce channel for quotes
 *   <li>'TICKET_CHANNEL_NAME' the name of the tickets channel. Please note that it will try to
 *       rename the ticket every time it changes the status or type
 *       <p>This includes the placeholders:
 *       <ul>
 *         <li>%creator% the creator of the ticket
 *         <li>%type% the type of ticket
 *         <li>%id% the id of the ticket
 *       </ul>
 *   <li>'CREATOR_TITLE' The title for the message send in the make of a ticket creator
 *   <li>'CREATOR_DESCRIPTION' The description for the message send in the make of a ticket creator
 *       for both of the messages above includes the place holders
 *       <ul>
 *         <li>%creator% the name of the creator of the ticket
 *         <li>%id% the id of the ticket
 *       </ul>
 *   <li>'MENTION_ROLE' If it is a question for roles and there's no roles in the answer
 *   <li>'ROLE_LIMIT' If it is a question for roles and there's more roles mentioned that in the
 *       limit
 *   <li>'NOT_MENTIONABLE' If one of the roles mentioned is not mentionable
 *   <li>'TOO_LONG_ANSWER' If the answer of a question is longer than the limit
 *       <p>This includes the placeholders
 *       <ul>
 *         <li>%limit% the limit of characters for the response
 *       </ul>
 *   <li>'MORE_THAN_LIMIT' IF the user is trying to create more tickets than the limit allows to
 *       <p>This includes the placeholders
 *       <ul>
 *         <li>%limit% the limit of tickets to have open
 *       </ul>
 *   <li>'TICKET_CLOSING_IN_TITLE' The title of the embed sent when the ticket is announced to be
 *       closed
 *   <li>'TICKET_CLOSING_IN_DESCRIPTION' The title of the embed sent when the ticket is announced to
 *       be closed
 *   <li>'TICKET_CLOSING_TITLE' The title of the embed when a ticket is closing
 *   <li>'TICKET_CLOSING_DESCRIPTION' The description when the ticket is closing 15 seconds is
 *       hardcoded
 *   <li>IMPORTANT: For your questions in 'config.json' the simple is a key for when the ticket is
 *       going to be announced it will get the get the title for the field using it. This means that
 *       if you have a question with a simple 'test' in your 'lang.properties' you must have a key
 *       with the same name (test) and the title of the field
 * </ul>
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Lang {

  /** Constant values to be used across the bot */
  @NotNull public static final String GUILD_NOT_SET = "Guild has not been set";

  @NotNull private static final PropertiesConfiguration propertiesConfiguration;
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
    return raw == null ? "Null" : raw;
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
    return raw == null ? "Null" : Strings.buildMessage(raw, placeHolders);
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
