package com.starfishst.ethot.config.objects.responsive.type.freelancer;

import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.util.Unicode;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class TwoReviewReactionResponse extends ReviewReactionResponse {

  protected TwoReviewReactionResponse(@NotNull Freelancer freelancer, @NotNull User user) {
    super(freelancer, user);
  }

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
