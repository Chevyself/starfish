package com.starfishst.ethot.objects.responsive.type.freelancer;

import com.starfishst.ethot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/** A review of 5 to a freelancer */
public class FiveReviewReactionResponse extends ReviewReactionResponse {

  /**
   * Create an instance
   *
   * @param freelancer the freelancer going to be rated
   * @param user the user that's going to rate the freelancer
   */
  public FiveReviewReactionResponse(long freelancer, long user) {
    super(freelancer, user);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.FIVE;
  }

  @Override
  int value() {
    return 5;
  }
}
