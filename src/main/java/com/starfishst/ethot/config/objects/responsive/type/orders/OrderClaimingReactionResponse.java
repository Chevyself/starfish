package com.starfishst.ethot.config.objects.responsive.type.orders;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.config.objects.responsive.ReactionResponse;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.FreelancerJoinTicketException;
import com.starfishst.ethot.tickets.loader.TicketLoader;
import com.starfishst.ethot.tickets.type.Order;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Unicode;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public class OrderClaimingReactionResponse implements ReactionResponse {

  @Override
  public @NotNull String getUnicode() {
    return Unicode.WHITE_CHECK_MARK;
  }

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    TicketLoader loader = Main.getManager().getLoader();
    Order order = loader.getOrderByMessage(event.getMessageIdLong());
    Freelancer freelancer = loader.getFreelancer(event.getUser().getIdLong());
    if (order != null && freelancer != null) {
      try {
        order.setFreelancer(freelancer);
      } catch (FreelancerJoinTicketException | DiscordManipulationException e) {
        event
            .getUser()
            .openPrivateChannel()
            .queue(channel -> Messages.error(e.getMessage()).send(channel));
      }
    } else if (freelancer == null) {
      event
          .getUser()
          .openPrivateChannel()
          .queue(channel -> Messages.error(Lang.get("NOT_FREELANCER")).send(channel));
    }
  }
}
