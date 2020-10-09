package com.starfishst.bot.data.messages.offer;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.Starfish;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The reaction to accept an offer */
public class OfferAcceptReactionResponse implements ReactionResponse {

  /** The message of the offer */
  @NotNull private final OfferMessage message;

  /**
   * Create the reaction response
   *
   * @param message the message of the offer
   */
  public OfferAcceptReactionResponse(@NotNull OfferMessage message) {
    this.message = message;
  }

  @Override
  public boolean removeReaction() {
    return true;
  }

  @Override
  public void onReaction(@NotNull MessageReactionAddEvent event) {
    Ticket ticket =
        Starfish.getTicketManager()
            .getDataLoader()
            .getTicketByChannel(event.getChannel().getIdLong());
    if (ticket != null) {
      BotUser freelancer =
          Starfish.getTicketManager()
              .getDataLoader()
              .getStarfishUser(message.getData().getValueOr("freelancer", Long.class, -1L));
      if (!ticket.addUser(freelancer, "freelancer")) {
        TextChannel channel = ticket.getTextChannel();
        BotUser owner = ticket.getOwner();
        if (channel != null && owner != null) {
          Messages.build(
                  owner.getLocaleFile().get("offer.freelancer-cant-join", ticket.getPlaceholders()),
                  ResultType.ERROR,
                  owner)
              .send(channel, Messages.getErrorConsumer());
        }
      }
    }
  }

  @Override
  public @NotNull String getUnicode() {
    return Starfish.getLanguageHandler().getFile("en").get("unicode.accept-offer");
  }
}
