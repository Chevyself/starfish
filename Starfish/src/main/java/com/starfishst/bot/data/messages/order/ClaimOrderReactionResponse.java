package com.starfishst.bot.data.messages.order;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.lang.StarfishLocaleFile;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** Claim an order ticket */
public class ClaimOrderReactionResponse implements ReactionResponse {

  /** The message that is having the reaction */
  @NotNull private final ClaimOrderResponsiveMessage message;

  /**
   * Create the claim reaction response
   *
   * @param message the message to change
   */
  public ClaimOrderReactionResponse(@NotNull ClaimOrderResponsiveMessage message) {
    this.message = message;
  }

  @Override
  public boolean removeReaction() {
    return true;
  }

  @Override
  public void onReaction(@NotNull MessageReactionAddEvent event) {
    Ticket ticket =
        Starfish.getLoader().getTicket(message.getData().getValueOr("id", Long.class, -1L));
    BotUser user = Starfish.getLoader().getStarfishUser(event.getUserIdLong());
    StarfishLocaleFile locale = Starfish.getLanguageHandler().getDefault();
    if (ticket != null) {
      if (ticket.addUser(user, "freelancer") || ticket.hasFreelancers()) {
        EmbedQuery query = ticket.toCompleteInformation(locale, false);
        query.getEmbedBuilder().setTitle(locale.get("ticket.claimed.title"));
        query.getEmbedBuilder().setDescription(locale.get("ticket.claimed.desc"));
        query
            .getEmbedBuilder()
            .setColor(Starfish.getConfiguration().getManagerOptions().getError());
        event
            .getChannel()
            .editMessageById(event.getMessageIdLong(), query.getAsMessageQuery().getMessage())
            .queue();
      }
    }
  }

  @Override
  public @NotNull String getUnicode() {
    return Starfish.getLanguageHandler().getFile("en").get("unicode.claim-order");
  }
}
