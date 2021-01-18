package com.starfishst.bot.handlers.questions;

import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.commands.jda.result.ResultType;
import com.starfishst.commands.jda.utils.embeds.EmbedQuery;
import java.util.HashMap;
import lombok.NonNull;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * This represents a question. Used in {@link QuestionsHandler}
 *
 * <p>The representation in json:
 *
 * <p>{ "title": "Something here", "description": "Something descriptive", "simple": "Something",
 * "limit": 10 }
 */
public class Question {

  /** The limit of characters for the answer */
  protected final int limit;
  /** The title of the question */
  @NonNull private final String title;
  /** The simple of the question */
  @NonNull private final String simple;
  /** The description of the role */
  @NonNull private final String description;

  /**
   * Create a question
   *
   * @param title the title of the question
   * @param simple the simple of the question
   * @param description the description of the question
   * @param limit the limit of the question
   */
  public Question(
      @NonNull String title, @NonNull String simple, @NonNull String description, int limit) {
    this.limit = limit;
    if (simple.contains(" ")) {
      String error = "The 'simple' or summary of a question may not have spaces but got: " + simple;
      throw new IllegalArgumentException(error);
    }
    if (limit > MessageEmbed.TEXT_MAX_LENGTH) {
      String error =
          "The limit cannot be longer than Embeds limit: " + MessageEmbed.TEXT_MAX_LENGTH;
      throw new IllegalArgumentException(error);
    }
    this.title = title;
    this.simple = simple;
    this.description = description;
  }

  /**
   * Get the question as a query to send
   *
   * @param user the user that needs the query
   * @return the query to send
   */
  @NonNull
  public EmbedQuery getQuery(@NonNull BotUser user) {
    return Messages.build(
        this.getBuiltTitle(), this.getBuiltDescription(), ResultType.GENERIC, user);
  }

  /**
   * Get the answer given to this question from a message received event
   *
   * @param event the event of a message received
   * @param user the user that queried the answer
   * @return the answer given from the event of null if the answer is invalid
   */
  public Object getAnswer(@NonNull GuildMessageReceivedEvent event, @NonNull BotUser user) {
    String contentRaw = event.getMessage().getContentRaw();
    if (contentRaw.length() < this.limit) {
      return contentRaw;
    } else {
      Messages.build(
              user.getLocaleFile()
                  .get(
                      "questions.longer-than-limit",
                      Maps.singleton("limit", String.valueOf(this.limit))),
              ResultType.ERROR,
              user)
          .send(event.getChannel(), Messages.getErrorConsumer());
      return null;
    }
  }

  /**
   * The tile of a question
   *
   * @return the title
   */
  @NonNull
  public String getBuiltTitle() {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("limit", String.valueOf(this.limit));
    return Strings.build(this.title, placeholders);
  }

  /**
   * Get the raw title of the question
   *
   * @return the raw title of the question
   */
  @NonNull
  public String getTitle() {
    return this.title;
  }

  /**
   * A word to describe the question: Example:
   *
   * <p>Title: What's your email? Simple: email
   *
   * @return the simple of a question
   */
  @NonNull
  public String getSimple() {
    return this.simple;
  }

  /**
   * Get the limit of characters of an answer
   *
   * @return the limit of characters
   */
  public int getLimit() {
    return this.limit;
  }

  /**
   * Get the description of the question to help who's answering a little bit
   *
   * @return the description
   */
  @NonNull
  public String getBuiltDescription() {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("limit", String.valueOf(this.limit));
    return Strings.build(this.description, placeholders);
  }

  /**
   * Get the raw description of the question
   *
   * @return the raw description of the question
   */
  @NonNull
  public String getDescription() {
    return this.description;
  }

  @Override
  public String toString() {
    return "Question{"
        + "title='"
        + this.title
        + '\''
        + ", simple='"
        + this.simple
        + '\''
        + ", description='"
        + this.description
        + '\''
        + ", limit="
        + this.limit
        + '}';
  }
}
