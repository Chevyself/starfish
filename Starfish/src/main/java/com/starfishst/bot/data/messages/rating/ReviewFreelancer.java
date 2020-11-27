package com.starfishst.bot.data.messages.rating;

import com.starfishst.bot.Starfish;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import java.util.HashSet;
import java.util.List;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** A message used for freelancer to have a rating */
public class ReviewFreelancer extends StarfishResponsiveMessage {
  /**
   * Create the responsive message. This constructor should be used when creating the responsive
   * message from a configuration
   *
   * @param id the id of the responsive message
   * @param data the data of the message
   */
  public ReviewFreelancer(long id, @NotNull StarfishValuesMap data) {
    super(id, new HashSet<>(), "review", data);
    for (ReactionResponse response :
        getResponses(
            data.getValueOr("freelancer", Long.class, -1L),
            data.getValueOr("user", Long.class, -1L))) {
      this.addReactionResponse(response);
    }
  }

  /**
   * Create the responsive message. This constructor should be used in a message that was already
   * sent
   *
   * @param message the message to make the responsive message
   * @param freelancer the freelancer being reviewed
   * @param user the user that is reviewing the freelancer
   */
  public ReviewFreelancer(@NotNull Message message, long freelancer, long user) {
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

  @NotNull
  public static String getUnicode(@NotNull String key) {
    return Starfish.getLanguageHandler().getDefault().get(key);
  }
}
