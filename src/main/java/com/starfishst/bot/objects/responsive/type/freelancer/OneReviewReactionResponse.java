package com.starfishst.bot.objects.responsive.type.freelancer;

import com.starfishst.bot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/** A review of 1 to a freelancer */
public class OneReviewReactionResponse extends ReviewReactionResponse {

  /**
   * Create an instance
   *
   * @param freelancer the freelancer going to be rated
   * @param user the user that's going to rate the freelancer
   */
  public OneReviewReactionResponse(long freelancer, long user) {
    super(freelancer, user);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.getEmoji("UNICODE_1_REVIEW");
  }

  @Override
  int value() {
    return 1;
  }
}