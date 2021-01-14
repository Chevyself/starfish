package com.starfishst.bot.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** Claim an order ticket */
public class ClaimOrderReactionResponse extends StarfishReactionResponse {
  public ClaimOrderReactionResponse(@Nullable BotResponsiveMessage message) {
    super(message);
  }

  @Override
  public @NonNull String getType() {
    return "claim-order";
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    if (this.message == null) return true;
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
