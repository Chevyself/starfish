package com.starfishst.ethot.config.objects.responsive.type.quotes;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.objects.freelancers.Offer;
import com.starfishst.ethot.config.objects.responsive.ReactionResponse;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.FreelancerJoinTicketException;
import com.starfishst.ethot.tickets.type.Quote;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Unicode;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This is called when the customer clicks :white_check_mark: to accept the offer from a freelancer
 *
 * @author Chevy
 * @version 1.0.0
 */
public class OfferAcceptReactionResponse implements ReactionResponse {

  @Override
  public @NotNull String getUnicode() {
    return Unicode.WHITE_CHECK_MARK;
  }

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    Ticket ticket =
        Main.getManager().getLoader().getTicketByChannel(event.getChannel().getIdLong());
    if (ticket instanceof Quote) {
      Offer offer = ((Quote) ticket).getOfferByMessageId(event.getMessageIdLong());
      if (offer != null) {
        try {
          ((Quote) ticket).setFreelancer(offer.getFreelancer());
        } catch (FreelancerJoinTicketException | DiscordManipulationException e) {
          Messages.error(e.getMessage()).send(event.getChannel());
        }
      }
    }
  }
}
