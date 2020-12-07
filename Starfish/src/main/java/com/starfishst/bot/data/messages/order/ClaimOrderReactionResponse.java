package com.starfishst.bot.data.messages.order;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** Claim an order ticket */
public class ClaimOrderReactionResponse implements ReactionResponse {

  /** The message that is having the reaction */
  @NonNull private final ClaimOrderResponsiveMessage message;

  /**
   * Create the claim reaction response
   *
   * @param message the message to change
   */
  public ClaimOrderReactionResponse(@NonNull ClaimOrderResponsiveMessage message) {
    this.message = message;
  }

  @Override
  public boolean removeReaction() {
    return false;
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    Ticket ticket =
        Starfish.getLoader().getTicket(message.getData().getValueOr("id", Long.class, -1L));
    BotUser user = Starfish.getLoader().getStarfishUser(event.getUserIdLong());
    LocaleFile locale = Starfish.getLanguageHandler().getDefault();
    if (ticket != null) {
      if (ticket.addUser(user, "freelancer") || ticket.hasFreelancers()) {
        EmbedQuery query = ticket.toCompleteInformation(locale, false);
        query.setTitle(locale.get("ticket.claimed.title", ticket.getPlaceholders()));
        query.setDescription(locale.get("ticket.claimed.desc", ticket.getPlaceholders()));
        query.setColor(Starfish.getConfiguration().getManagerOptions().getError());
        event
            .getChannel()
            .editMessageById(event.getMessageIdLong(), query.getAsMessageQuery().build())
            .queue();
      }
    }
    return true;
  }

  @Override
  public @NonNull String getUnicode() {
    return Starfish.getLanguageHandler().getFile("en").get("unicode.claim-order");
  }
}
