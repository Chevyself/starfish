package com.starfishst.ethot.config.objects.responsive.type.freelancer;

import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class ReviewFreelancer extends ResponsiveMessage {

  private final long freelancer;
  private final long user;

  public ReviewFreelancer(long id, @NotNull Freelancer freelancer, @NotNull User user) {
    super(
        ResponsiveMessageType.REVIEW,
        id,
        true,
        new OneReviewReactionResponse(freelancer, user),
        new TwoReviewReactionResponse(freelancer, user),
        new ThreeReviewReactionResponse(freelancer, user),
        new FourReviewReactionResponse(freelancer, user),
        new FiveReviewReactionResponse(freelancer, user));
    this.freelancer = freelancer.getId();
    this.user = user.getIdLong();
  }

  public ReviewFreelancer(long id, long freelancer, long user) {
    super(
        ResponsiveMessageType.REVIEW,
        id,
        true,
        new OneReviewReactionResponse(freelancer, user),
        new TwoReviewReactionResponse(freelancer, user),
        new ThreeReviewReactionResponse(freelancer, user),
        new FourReviewReactionResponse(freelancer, user),
        new FiveReviewReactionResponse(freelancer, user));
    this.freelancer = freelancer;
    this.user = user;
  }

  public long getFreelancer() {
    return freelancer;
  }

  public long getUser() {
    return user;
  }
}
