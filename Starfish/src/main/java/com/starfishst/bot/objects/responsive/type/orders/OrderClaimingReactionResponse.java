package com.starfishst.bot.objects.responsive.type.orders;

import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.FreelancerJoinTicketException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.oldtickets.TicketManager;
import com.starfishst.bot.oldtickets.loader.TicketLoader;
import com.starfishst.bot.oldtickets.type.Order;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Unicode;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** This is the action when the freelancer tries to claim the ticket */
public class OrderClaimingReactionResponse implements ReactionResponse {

  @Override
  public @NotNull String getUnicode() {
    return Unicode.getEmoji("UNICODE_ORDER_CLAIMING");
  }

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    TicketLoader loader = TicketManager.getInstance().getLoader();
    Order order = loader.getOrderByMessage(event.getMessageIdLong());
    Freelancer freelancer = loader.getFreelancer(event.getUser().getIdLong());
    if (order != null && freelancer != null) {
      try {
        if (order.setFreelancer(freelancer)) {
          TextChannel channel = event.getChannel();
          channel
              .removeReactionById(
                  event.getMessageIdLong(), getUnicode(), event.getJDA().getSelfUser())
              .queue();
          channel
              .editMessageById(
                  event.getMessageIdLong(),
                  Messages.claimed(order, "ORDER_CLAIMED_TITLE", "ORDER_CLAIMED_DESCRIPTION")
                      .getMessage())
              .queue();
        }
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
