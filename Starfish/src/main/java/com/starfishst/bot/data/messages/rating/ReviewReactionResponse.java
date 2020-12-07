package com.starfishst.bot.data.messages.rating;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.api.utility.Messages;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The reaction response created to review a freelancer */
public class ReviewReactionResponse implements ReactionResponse {

  /** The freelancer that is being reviewed */
  private final long freelancer;

  /** The user that is reviewing the freelancer */
  private final long user;

  /** The unicode that will be used in the reaction */
  @NonNull private final String unicode;

  /** The value of the response */
  private final int value;

  /**
   * Create the reaction response
   *
   * @param freelancer the freelancer that is being reviewed
   * @param user the user that is reviewing the freelancer
   * @param unicode the unicode
   * @param value the value of the rating
   */
  public ReviewReactionResponse(long freelancer, long user, @NonNull String unicode, int value) {
    this.freelancer = freelancer;
    this.user = user;
    this.unicode = unicode;
    this.value = value;
  }

  @Override
  public boolean removeReaction() {
    return true;
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    if (event.getUserIdLong() != user) return true;
    DataLoader loader = Starfish.getLoader();
    BotUser user = loader.getStarfishUser(event.getUserIdLong());
    BotUser freelancer = loader.getStarfishUser(this.freelancer);
    if (!freelancer.isFreelancer()) return true;
    FreelancerRating rating = loader.getRating(this.freelancer);
    event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
    rating.addRating(event.getUserIdLong(), this.value);
    Messages.build(
            user.getLocaleFile().get("thanks-rating", freelancer.getPlaceholders()),
            ResultType.GENERIC,
            user)
        .send(event.getChannel());
    return false;
  }

  @Override
  public @NonNull String getUnicode() {
    return this.unicode;
  }
}
