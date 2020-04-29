package com.starfishst.ethot.objects.responsive.type.freelancer;

import com.starfishst.ethot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/** A review of 4 to a freelancer */
public class FourReviewReactionResponse extends ReviewReactionResponse {

  /**
   * Create an instance
   *
   * @param freelancer the freelancer going to be rated
   * @param user the user that's going to rate the freelancer
   */
  public FourReviewReactionResponse(long freelancer, long user) {
    super(freelancer, user);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.getEmoji("UNICODE_4_REVIEW");
  }

  @Override
  int value() {
    return 4;
  }
}
