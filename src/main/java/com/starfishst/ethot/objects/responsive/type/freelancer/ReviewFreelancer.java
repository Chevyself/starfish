package com.starfishst.ethot.objects.responsive.type.freelancer;

import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.responsive.ReactionResponse;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.simple.Lots;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** The message asking for the review of a {@link Freelancer} */
public class ReviewFreelancer extends ResponsiveMessage {

  /** The id of the freelancer */
  private final long freelancer;
  /** The id of the user allowed to review */
  private final long user;

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   * @param freelancer the freelancer that is being rated
   * @param user the user rating the freelancer
   */
  public ReviewFreelancer(@NotNull Message message, long freelancer, long user) {
    super(ResponsiveMessageType.REVIEW, message, getReactions(freelancer, user), true);
    this.freelancer = freelancer;
    this.user = user;
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param id the id of the message
   * @param freelancer the freelancer that is being rated
   * @param user the user rating the freelancer
   */
  public ReviewFreelancer(long id, long freelancer, long user) {
    super(ResponsiveMessageType.REVIEW, id, getReactions(freelancer, user), true);
    this.freelancer = freelancer;
    this.user = user;
  }

  /**
   * Get the reactions that will be used in this message
   *
   * @param freelancer the freelancer that is being rated
   * @param user the user rating the freelancer
   * @return the list of reactions
   */
  @NotNull
  private static List<ReactionResponse> getReactions(long freelancer, long user) {
    return Lots.list(
        new OneReviewReactionResponse(freelancer, user),
        new TwoReviewReactionResponse(freelancer, user),
        new ThreeReviewReactionResponse(freelancer, user),
        new FourReviewReactionResponse(freelancer, user),
        new FiveReviewReactionResponse(freelancer, user));
  }

  /**
   * Get the freelancer that is being rated
   *
   * @return the id of the freelancer that is being rated
   */
  public long getFreelancer() {
    return freelancer;
  }

  /**
   * Get the user that is rating the freelancer
   *
   * @return the id of the user that is rating the freelancer
   */
  public long getUser() {
    return user;
  }
}
