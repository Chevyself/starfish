package com.starfishst.bot.objects.questions;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.exception.TicketCreationException;
import com.starfishst.core.utils.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * If the {@link Question#getSimple()} starts with 'image-' it will be a question image which means
 * that the answer must be a valid image URL {@link net.dv8tion.jda.api.EmbedBuilder#URL_PATTERN}
 */
public class QuestionImage extends Question {

  /**
   * Create a question
   *
   * @param title the title of the question
   * @param simple the simple of the question
   * @param description the description of the question
   * @param limit the limit of the question
   */
  public QuestionImage(
      @NotNull String title, @NotNull String simple, @NotNull String description, int limit) {
    super(title, simple, description, limit);
  }

  /**
   * Checks that the url provided is valid to use in embeds
   *
   * @param url the embed to validate
   * @throws TicketCreationException if the embed is not valid
   */
  public static void checkUrl(@NotNull String url) throws TicketCreationException {
    if (!EmbedBuilder.URL_PATTERN.matcher(url).matches()) {
      throw new TicketCreationException(Lang.get("URL_NOT_MATCH", Maps.singleton("url", url)));
    }
  }
}
