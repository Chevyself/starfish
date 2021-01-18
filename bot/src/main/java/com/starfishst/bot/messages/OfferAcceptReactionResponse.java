package com.starfishst.bot.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.messages.StarfishReactionResponse;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.commands.jda.result.ResultType;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The reaction to accept an offer */
public class OfferAcceptReactionResponse extends StarfishReactionResponse {

  public OfferAcceptReactionResponse(@Nullable BotResponsiveMessage message) {
    super(message);
  }

  @Override
  public @NonNull String getType() {
    return "offer";
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    if (this.message == null) return true;
    Ticket ticket =
        Starfish.getTicketManager()
            .getDataLoader()
            .getTicketByChannel(event.getChannel().getIdLong());
    if (ticket != null) {
      BotUser freelancer =
          Starfish.getTicketManager()
              .getDataLoader()
              .getStarfishUser(this.message.getData().getOr("freelancer", Long.class, -1L));
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
    return true;
  }

  @Override
  public @NonNull String getUnicode() {
    return Starfish.getLanguageHandler().getFile("en").get("unicode.accept-offer");
  }
}
