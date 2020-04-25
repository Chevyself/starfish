package com.starfishst.ethot.config.objects.responsive.type.freelancer;

import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.util.Unicode;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class FourReviewReactionResponse extends ReviewReactionResponse {

  protected FourReviewReactionResponse(@NotNull Freelancer freelancer, @NotNull User user) {
    super(freelancer, user);
  }

  public FourReviewReactionResponse(long freelancer, long user) {
    super(freelancer, user);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.FOUR;
  }

  @Override
  int value() {
    return 4;
  }
}
