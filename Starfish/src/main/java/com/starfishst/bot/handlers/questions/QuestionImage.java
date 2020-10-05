package com.starfishst.bot.handlers.questions;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.utils.maps.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
   * @return true if it is a valid url
   */
  public boolean checkUrl(@NotNull String url) {
    return EmbedBuilder.URL_PATTERN.matcher(url).matches();
  }

  @Override
  public @Nullable String getAnswer(
      @NotNull GuildMessageReceivedEvent event, @NotNull BotUser user) {
    String raw = event.getMessage().getContentRaw();
    if (this.checkUrl(raw)) {
      return raw;
    } else {
      Messages.build(
              user.getLocaleFile().get("questions.invalid-image-url", Maps.singleton("url", raw)),
              ResultType.ERROR,
              user)
          .send(event.getChannel());
      return null;
    }
  }

  @Override
  public @NotNull String getSimple() {
    return super.getSimple();
  }
}
