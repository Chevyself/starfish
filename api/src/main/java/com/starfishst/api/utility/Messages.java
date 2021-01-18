package com.starfishst.api.utility;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.user.BotUser;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedFactory;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Message;

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
  @NonNull
  public static EmbedQuery build(
      String title,
      String description,
      String thumbnail,
      String image,
      String footer,
      ResultType type,
      LinkedHashMap<String, String> fields,
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
  @NonNull
  public static String getThumbnail(@NonNull LocaleFile locale) {
    return locale.get("thumbnail-url");
  }

  /**
   * Get the thumbnail for certain bot user
   *
   * @param locale the locale to get the thumbnail from
   * @return the thumbnail for the user
   */
  @NonNull
  public static String getFooter(@NonNull LocaleFile locale) {
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
      @NonNull String title,
      @NonNull String description,
      @NonNull ResultType type,
      @NonNull LocaleFile locale) {
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
  @NonNull
  public static EmbedQuery build(
      @NonNull String title,
      @NonNull String description,
      @NonNull ResultType type,
      @NonNull BotUser user) {
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
  @NonNull
  public static EmbedQuery build(
      @NonNull String description, @NonNull ResultType type, @NonNull LocaleFile locale) {
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
  @NonNull
  public static EmbedQuery build(
      @NonNull String description, @NonNull ResultType type, @NonNull BotUser user) {
    return Messages.build(description, type, user.getLocaleFile());
  }

  /**
   * Build a query
   *
   * @param titleKey the key of the title
   * @param descKey the key of the description
   * @param placeholders the placeholders to replace the title and the description
   * @param viewer the language to get the title and the description
   * @return the built message
   */
  @NonNull
  public static EmbedQuery build(
      @NonNull String titleKey,
      @NonNull String descKey,
      @NonNull Map<String, String> placeholders,
      @NonNull LocaleFile viewer) {
    return Messages.build(
        viewer.get(titleKey, placeholders),
        viewer.get(descKey, placeholders),
        ResultType.GENERIC,
        viewer);
  }

  /**
   * Get the information of an user for a viewer
   *
   * @param user the user to get the information of
   * @param viewer the user that will see the information
   * @return the built embed query
   */
  @NonNull
  public static EmbedQuery build(@NonNull BotUser user, @NonNull BotUser viewer) {
    Map<String, String> placeholders = user.getPlaceholders();
    EmbedQuery build =
        build("user-info.title", "user-info.description", placeholders, viewer.getLocaleFile());
    user.getPreferences()
        .getMap()
        .forEach(
            (key, value) -> {
              if (key.equalsIgnoreCase("lang") || key.equalsIgnoreCase("freelancer")) return;
              if (value instanceof Collection) {
                build.addField(key, Lots.pretty((Collection<?>) value), true);
              } else {
                build.addField(key, value.toString(), true);
              }
            });
    if (user.isFreelancer())
      build.addField("Rating", user.getRating().getReadable(viewer.getLocaleFile()), true);
    return build;
  }

  /**
   * Get the consumer for errors
   *
   * @return the consumer for errors
   */
  public static Consumer<Message> getErrorConsumer() {
    return Starfish.getCommandManager().getListener().getConsumer(new Result(ResultType.ERROR, ""));
  }
}
