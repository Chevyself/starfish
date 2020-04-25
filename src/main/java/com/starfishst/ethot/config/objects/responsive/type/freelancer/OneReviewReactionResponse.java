package com.starfishst.ethot.config.objects.responsive.type.freelancer;

import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.util.Unicode;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class OneReviewReactionResponse extends ReviewReactionResponse {

  protected OneReviewReactionResponse(Freelancer freelancer, @NotNull User user) {
    super(freelancer, user);
  }

  public OneReviewReactionResponse(long freelancer, long user) {
    super(freelancer, user);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.ONE;
  }

  @Override
  int value() {
    return 1;
  }
}
