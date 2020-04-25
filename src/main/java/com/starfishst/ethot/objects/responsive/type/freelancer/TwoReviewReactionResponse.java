package com.starfishst.ethot.objects.responsive.type.freelancer;

import com.starfishst.ethot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/** A review of 2 to a freelancer */
public class TwoReviewReactionResponse extends ReviewReactionResponse {

  /**
   * Create an instance
   *
   * @param freelancer the freelancer going to be rated
   * @param user the user that's going to rate the freelancer
   */
  public TwoReviewReactionResponse(long freelancer, long user) {
    super(freelancer, user);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.TWO;
  }

  @Override
  int value() {
    return 2;
  }
}
