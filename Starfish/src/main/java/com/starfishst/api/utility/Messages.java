package com.starfishst.api.utility;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.bot.Starfish;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedFactory;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.LinkedHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Messages {

  /**
   * Create an embed query
   *
   * @param title the title of the embed
   * @param description the description of the embed
   * @param thumbnail the thumbnail of the embed
   * @param image the image of the embed
   * @param footer the footer of the embed
   * @param type the type to get the color from
   * @param fields the fields of the embed
   * @param inline whether the fields must be inline
   * @return the embed query
   */
  @NotNull
  public static EmbedQuery build(
      @Nullable String title,
      @Nullable String description,
      @Nullable String thumbnail,
      @Nullable String image,
      @Nullable String footer,
      @Nullable ResultType type,
      @Nullable LinkedHashMap<String, String> fields,
      boolean inline) {
    return EmbedFactory.newEmbed(
        title,
        description,
        thumbnail,
        image,
        footer,
        type == null ? null : type.getColor(Starfish.getConfiguration().getManagerOptions()),
        fields,
        inline);
  }

  /**
   * Get the thumbnail for certain bot user
   *
   * @param locale the locale to get the thumbnail from
   * @return the thumbnail for the user
   */
  @NotNull
  public static String getThumbnail(@NotNull LocaleFile locale) {
    return locale.get("thumbnail-url");
  }

  /**
   * Get the thumbnail for certain bot user
   *
   * @param locale the locale to get the thumbnail from
   * @return the thumbnail for the user
   */
  @NotNull
  public static String getFooter(@NotNull LocaleFile locale) {
    return locale.get("footer");
  }

  /**
   * Builds a message
   *
   * @param title the title of the embed
   * @param description the description of the embed
   * @param type the type to get the color of the embed
   * @param locale the locale that will read the message
   * @return the embed query
   */
  public static EmbedQuery build(
      @NotNull String title,
      @NotNull String description,
      @NotNull ResultType type,
      @NotNull LocaleFile locale) {
    return Messages.build(
        title,
        description,
        Messages.getThumbnail(locale),
        null,
        Messages.getFooter(locale),
        type,
        null,
        false);
  }

  /**
   * Builds a message
   *
   * @param title the title of the embed
   * @param description the description of the embed
   * @param type the type to get the color of the embed
   * @param user the user that will read the message
   * @return the embed query
   */
  public static EmbedQuery build(
      @NotNull String title,
      @NotNull String description,
      @NotNull ResultType type,
      @NotNull BotUser user) {
    return Messages.build(title, description, type, user.getLocaleFile());
  }

  /**
   * Builds a message
   *
   * @param description the description of the embed
   * @param type the type to get the color of the embed
   * @param locale the user locale will read the message
   * @return the embed query
   */
  public static EmbedQuery build(
      @NotNull String description, @NotNull ResultType type, @NotNull LocaleFile locale) {
    return Messages.build(
        type.getTitle(Starfish.getLanguageHandler(), null),
        description,
        Messages.getThumbnail(locale),
        null,
        Messages.getFooter(locale),
        type,
        null,
        false);
  }

  /**
   * Builds a message
   *
   * @param description the description of the embed
   * @param type the type to get the color of the embed
   * @param user the user that will read the message
   * @return the embed query
   */
  public static EmbedQuery build(
      @NotNull String description, @NotNull ResultType type, @NotNull BotUser user) {
    return Messages.build(description, type, user.getLocaleFile());
  }
}
