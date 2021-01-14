package com.starfishst.bot.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The ticket creator reaction response */
public class TicketCreatorReactionResponse extends StarfishReactionResponse {

  @NonNull @Getter private final TicketType ticketType;

  protected TicketCreatorReactionResponse(
      @Nullable BotResponsiveMessage message, @NonNull TicketType ticketType) {
    super(message);
    this.ticketType = ticketType;
  }

  @NonNull
  public static BotResponsiveMessage add(
      @NonNull BotResponsiveMessage responsiveMessage, @Nullable Message message) {
    for (TicketType ticketType :
        Lots.list(
            TicketType.ORDER,
            TicketType.APPLY,
            TicketType.SUPPORT,
            TicketType.REPORT,
            TicketType.SUGGESTION)) {
      TicketCreatorReactionResponse response =
          new TicketCreatorReactionResponse(responsiveMessage, ticketType);
      if (message != null) {
        responsiveMessage.addReactionResponse(response, message);
      } else {
        responsiveMessage.addReactionResponse(response);
      }
    }
    return responsiveMessage;
  }

  @Override
  public @NonNull String getType() {
    return "ticket-creator";
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    if (message == null) return true;
    BotUser user = Starfish.getLoader().getStarfishUser(event.getUserIdLong());
    Ticket ticket =
        Starfish.getLoader().getTicket(message.getData().getValueOr("ticket", Long.class, -1L));
    if (ticket != null) {
      try {
        Starfish.getTicketManager().createTicket(ticketType, user, ticket);
      } catch (TicketCreationException e) {
        e.toQuery(user).send(event.getTextChannel(), Messages.getErrorConsumer());
      }
    }
    return true;
  }

  @Override
  public @NonNull String getUnicode() {
    return getUnicode("unicode.tickets." + ticketType.name().toLowerCase());
  }
}
