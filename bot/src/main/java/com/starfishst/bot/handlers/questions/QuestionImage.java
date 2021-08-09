package com.starfishst.bot.handlers.questions;

import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import java.util.Collections;
import lombok.NonNull;
import me.googas.commands.jda.result.ResultType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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
      @NonNull String title, @NonNull String simple, @NonNull String description, int limit) {
    super(title, simple, description, limit);
  }

  /**
   * Checks that the url provided is valid to use in embeds
   *
   * @param url the embed to validate
   * @return true if it is a valid url
   */
  public boolean checkUrl(@NonNull String url) {
    return EmbedBuilder.URL_PATTERN.matcher(url).matches();
  }

  @Override
  public String getAnswer(@NonNull GuildMessageReceivedEvent event, @NonNull BotUser user) {
    String raw = event.getMessage().getContentRaw();
    if (this.checkUrl(raw)) {
      return raw;
    } else {
      event
          .getChannel()
          .sendMessageEmbeds(
              Messages.build(
                      user.getLocaleFile()
                          .get("questions.invalid-image-url", Collections.singletonMap("url", raw)),
                      ResultType.ERROR,
                      user)
                  .build())
          .queue(Messages.getErrorConsumer());
      return null;
    }
  }

  @Override
  public @NonNull String getSimple() {
    return super.getSimple();
  }
}
