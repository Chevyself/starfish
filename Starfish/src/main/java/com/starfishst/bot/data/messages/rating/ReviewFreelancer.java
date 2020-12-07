package com.starfishst.bot.data.messages.rating;

import com.starfishst.api.Starfish;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import java.util.HashSet;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Message;

/** A message used for freelancer to have a rating */
public class ReviewFreelancer extends StarfishResponsiveMessage {

  /**
   * Create the responsive message. This constructor should be used in a message that was already
   * sent
   *
   * @param message the message to make the responsive message
   * @param freelancer the freelancer being reviewed
   * @param user the user that is reviewing the freelancer
   */
  public ReviewFreelancer(@NonNull Message message, long freelancer, long user) {
    super(message, new HashSet<>(), "review", new StarfishValuesMap());
    this.getData().addValue("freelancer", freelancer);
    this.getData().addValue("user", user);
    for (ReactionResponse response : getResponses(freelancer, user)) {
      this.addReactionResponse(response, message);
    }
  }

  /**
   * Get the responses that will be used to review the freelancer
   *
   * @param freelancer the freelancer that is going to be reviewed
   * @param user the user that is reviewing the freelancer
   * @return the list of reaction responses
   */
  public static List<ReactionResponse> getResponses(long freelancer, long user) {
    return Lots.list(
        new ReviewReactionResponse(freelancer, user, getUnicode("review.1"), 1),
        new ReviewReactionResponse(freelancer, user, getUnicode("review.2"), 2),
        new ReviewReactionResponse(freelancer, user, getUnicode("review.3"), 3),
        new ReviewReactionResponse(freelancer, user, getUnicode("review.4"), 4),
        new ReviewReactionResponse(freelancer, user, getUnicode("review.5"), 5));
  }

  @NonNull
  public static String getUnicode(@NonNull String key) {
    return Starfish.getLanguageHandler().getDefault().get(key);
  }
}
