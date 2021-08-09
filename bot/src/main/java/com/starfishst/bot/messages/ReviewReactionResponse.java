package com.starfishst.bot.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.messages.StarfishReactionResponse;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.user.FreelancerRating;
import com.starfishst.api.utility.Messages;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commands.jda.result.ResultType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The reaction response created to review a freelancer */
public class ReviewReactionResponse extends StarfishReactionResponse {

  @Getter private final int value;

  protected ReviewReactionResponse(BotResponsiveMessage message, int value) {
    super(message);
    this.value = value;
  }

  @NonNull
  public static BotResponsiveMessage add(
      @NonNull BotResponsiveMessage responsiveMessage, Message message) {
    for (int i = 0; i < 5; i++) {
      ReviewReactionResponse response = new ReviewReactionResponse(responsiveMessage, i + 1);
      if (message != null) {
        responsiveMessage.addReactionResponse(response, message);
      } else {
        responsiveMessage.addReactionResponse(response);
      }
    }
    return responsiveMessage;
  }

  private long getFreelancer() {
    return this.message == null ? -1 : this.message.getData().getOr("freelancer", Long.class, -1L);
  }

  private long getUser() {
    return this.message == null ? -1 : this.message.getData().getOr("user", Long.class, -1L);
  }

  @Override
  public @NonNull String getType() {
    return "review";
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    if (event.getUserIdLong() != this.getUser()) return true;
    Loader loader = Starfish.getLoader();
    BotUser user = loader.getStarfishUser(event.getUserIdLong());
    BotUser freelancer = loader.getStarfishUser(this.getFreelancer());
    if (!freelancer.isFreelancer()) return true;
    FreelancerRating rating = loader.getRating(this.getFreelancer());
    event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
    rating.addRating(event.getUserIdLong(), this.value);
    event
        .getChannel()
        .sendMessageEmbeds(
            Messages.build(
                    user.getLocaleFile().get("thanks-rating", freelancer.getPlaceholders()),
                    ResultType.GENERIC,
                    user)
                .build())
        .queue();
    return false;
  }

  @Override
  public @NonNull String getUnicode() {
    return StarfishReactionResponse.getUnicode("review." + this.value);
  }
}
