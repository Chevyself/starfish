package com.starfishst.bot.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.messages.StarfishReactionResponse;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** Claim an order ticket */
public class ClaimOrderReactionResponse extends StarfishReactionResponse {
  public ClaimOrderReactionResponse(BotResponsiveMessage message) {
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
        Starfish.getLoader()
            .getTicket(this.message.getData().getOr("id", Number.class, -1L).longValue());
    BotUser user = Starfish.getLoader().getStarfishUser(event.getUserIdLong());
    LocaleFile locale = Starfish.getLanguageHandler().getDefault();
    if (ticket != null) {
      if (ticket.addUser(user, "freelancer") || ticket.hasFreelancers()) {
        EmbedBuilder builder = ticket.toCompleteInformation(locale, false);
        builder.setTitle(locale.get("ticket.claimed.title", ticket.getPlaceholders()));
        builder.setDescription(locale.get("ticket.claimed.desc", ticket.getPlaceholders()));
        // builder.setColor(Starfish.getConfiguration().getListenerOptions().getError());
        event
            .getChannel()
            .editMessageById(event.getMessageIdLong(), new MessageBuilder(builder).build())
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
