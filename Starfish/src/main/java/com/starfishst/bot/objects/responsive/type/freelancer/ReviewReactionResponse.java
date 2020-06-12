package com.starfishst.bot.objects.responsive.type.freelancer;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.util.Freelancers;
import com.starfishst.bot.util.Messages;
import java.util.HashMap;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The reaction response for the reviews */
public abstract class ReviewReactionResponse implements ReactionResponse {

  /** The freelancer being rated */
  private final long freelancer;
  /** The user being rated */
  private final long user;

  /**
   * Create an instance
   *
   * @param freelancer the freelancer going to be rated
   * @param user the user that's going to rate the freelancer
   */
  protected ReviewReactionResponse(long freelancer, long user) {
    this.freelancer = freelancer;
    this.user = user;
  }

  /**
   * Get the value to add as a rating to the freelancer
   *
   * @return the value
   */
  abstract int value();

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    if (event.getUser().getIdLong() == user) {
      Freelancer freelancer =
          TicketManager.getInstance().getLoader().getFreelancer(this.freelancer);
      if (freelancer != null) {
        if (freelancer.getRating().containsKey(event.getUser().getIdLong())) {
          HashMap<String, String> placeholders = Freelancers.getPlaceholders(freelancer);
          placeholders.put("user", event.getUser().getName());
          Messages.error(Lang.get("ALREADY_RATED", placeholders));
        } else {
          freelancer.addRating(event.getUser().getIdLong(), value());
          event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
          ResponsiveMessage responsiveMessage =
              Configuration.getInstance().getResponsiveMessage(event.getMessageIdLong());
          if (responsiveMessage != null) {
            responsiveMessage.remove();
          }
        }
      }
    }
  }
}
