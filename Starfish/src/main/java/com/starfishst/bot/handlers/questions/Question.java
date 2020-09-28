package com.starfishst.bot.handlers.questions;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedQuery;
import com.starfishst.core.utils.Strings;
import com.starfishst.core.utils.maps.Maps;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This represents a question. Used in {@link com.starfishst.bot.oldtickets.type.QuestionsTicket}
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
  @NotNull private final String title;
  /** The simple of the question */
  @NotNull private final String simple;
  /** The description of the role */
  @NotNull private final String description;

  /**
   * Create a question
   *
   * @param title the title of the question
   * @param simple the simple of the question
   * @param description the description of the question
   * @param limit the limit of the question
   */
  public Question(
      @NotNull String title, @NotNull String simple, @NotNull String description, int limit) {
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
   * The tile of a question
   *
   * @return the title
   */
  @NotNull
  public String getBuiltTitle() {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("limit", String.valueOf(limit));
    return Strings.buildMessage(title, placeholders);
  }

  /**
   * Get the raw title of the question
   *
   * @return the raw title of the question
   */
  @NotNull
  public String getTitle() {
    return title;
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
  public String getBuiltDescription() {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("limit", String.valueOf(limit));
    return Strings.buildMessage(description, placeholders);
  }

  /**
   * Get the raw description of the question
   *
   * @return the raw description of the question
   */
  @NotNull
  public String getDescription() {
    return description;
  }

  /**
   * Get the limit of characters of an answer
   *
   * @return the limit of characters
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Get the question as a query to send
   *
   * @param user the user that needs the query
   * @return the query to send
   */
  @NotNull
  public EmbedQuery getQuery(@NotNull BotUser user) {
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
  @Nullable
  public Object getAnswer(@NotNull GuildMessageReceivedEvent event, @NotNull BotUser user) {
    String contentRaw = event.getMessage().getContentRaw();
    if (contentRaw.length() < limit) {
      return contentRaw;
    } else {
      Messages.build(
              user.getLocaleFile()
                  .get(
                      "questions.longer-than-limit",
                      Maps.singleton("limit", String.valueOf(limit))),
              ResultType.ERROR,
              user)
          .send(event.getChannel());
      return null;
    }
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
