package com.starfishst.bot.objects.responsive.type.quotes;

import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.FreelancerJoinTicketException;
import com.starfishst.bot.objects.freelancers.Offer;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.type.Quote;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Unicode;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This is called when the customer clicks :white_check_mark: to accept the offer from a freelancer
 */
public class OfferAcceptReactionResponse implements ReactionResponse {

  @Override
  public @NotNull String getUnicode() {
    return Unicode.getEmoji("UNICODE_OFFER_ACCEPT");
  }

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    Ticket ticket =
        TicketManager.getInstance().getLoader().getTicketByChannel(event.getChannel().getIdLong());
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
