package com.starfishst.ethot.config.objects.responsive.type.freelancer;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.config.objects.responsive.ReactionResponse;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.util.Freelancers;
import com.starfishst.ethot.util.Messages;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public abstract class ReviewReactionResponse implements ReactionResponse {

  private final long freelancer;
  private final long user;

  protected ReviewReactionResponse(@NotNull Freelancer freelancer, @NotNull User user) {
    this(freelancer.getId(), user.getIdLong());
  }

  protected ReviewReactionResponse(long freelancer, long user) {
    this.freelancer = freelancer;
    this.user = user;
  }

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    if (event.getUser().getIdLong() == user) {
      Freelancer freelancer = Main.getManager().getLoader().getFreelancer(this.freelancer);
      if (freelancer.getRating().containsKey(event.getUser().getIdLong())) {
        HashMap<String, String> placeholders = Freelancers.getPlaceholders(freelancer);
        placeholders.put("user", event.getUser().getName());
        Messages.error(Lang.get("ALREADY_RATED", placeholders));
      } else {
        freelancer.addRating(event.getUser().getIdLong(), value());
        ResponsiveMessage responsiveMessage =
            Main.getConfiguration().getResponsiveMessage(event.getMessageIdLong());
        if (responsiveMessage != null) {
          Main.getConfiguration().removeResponsiveMessage(responsiveMessage);
        }
      }
    }
  }

  abstract int value();
}
