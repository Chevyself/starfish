package com.starfishst.bot.objects.responsive.type.freelancer;

import com.starfishst.bot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/** A review of 3 to a freelancer */
public class ThreeReviewReactionResponse extends ReviewReactionResponse {

  /**
   * Create an instance
   *
   * @param freelancer the freelancer going to be rated
   * @param user the user that's going to rate the freelancer
   */
  public ThreeReviewReactionResponse(long freelancer, long user) {
    super(freelancer, user);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.getEmoji("UNICODE_3_REVIEW");
  }

  @Override
  int value() {
    return 3;
  }
}