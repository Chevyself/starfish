package com.starfishst.ethot.config.objects.questions;

import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.Strings;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * This represents a question. Used in {@link com.starfishst.ethot.tickets.type.QuestionsTicket}
 *
 * <p>The representation in json:
 *
 * <p>{ "title": "Something here", "description": "Something descriptive", "simple": "Something",
 * "limit": 10 }
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Question {

  protected final int limit;
  @NotNull private final String title;
  @NotNull private final String simple;
  @NotNull private final String description;

  public Question(
      @NotNull String title, @NotNull String simple, @NotNull String description, int limit) {
    this.limit = limit;
    if (simple.contains(" ")) {
      String error = "The 'simple' or summary of a question may not have spaces but got: " + simple;
      Errors.addError(error);
      throw new IllegalArgumentException(error);
    }
    if (limit > MessageEmbed.TEXT_MAX_LENGTH) {
      String error =
          "The limit cannot be longer than Embeds limit: " + MessageEmbed.TEXT_MAX_LENGTH;
      Errors.addError(error);
      throw new IllegalArgumentException(error);
    }
    this.title = title;
    this.simple = simple;
    this.description = description;
  }

  /**
   * The tile of a question
   *
   * @return the title
   */
  @NotNull
  public String getTitle() {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("limit", String.valueOf(limit));
    return Strings.buildMessage(title, placeholders);
  }

  /**
   * A word to describe the question: Example:
   *
   * <p>Title: What's your email? Simple: email
   *
   * @return the simple of a question
   */
  @NotNull
  public String getSimple() {
    return simple;
  }

  /**
   * Get the description of the question to help who's answering a little bit
   *
   * @return the description
   */
  @NotNull
  public String getDescription() {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("limit", String.valueOf(limit));
    return Strings.buildMessage(description, placeholders);
  }

  /**
   * Get the limit of characters of an answer
   *
   * @return the limit of characters
   */
  public int getLimit() {
    return limit;
  }

  @Override
  public String toString() {
    return "Question{"
        + "title='"
        + title
        + '\''
        + ", simple='"
        + simple
        + '\''
        + ", description='"
        + description
        + '\''
        + ", limit="
        + limit
        + '}';
  }
}
