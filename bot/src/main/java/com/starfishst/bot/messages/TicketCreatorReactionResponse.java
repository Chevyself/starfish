package com.starfishst.bot.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.messages.StarfishReactionResponse;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import java.util.Arrays;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commands.jda.utils.responsive.ResponsiveMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The ticket creator reaction response */
public class TicketCreatorReactionResponse extends StarfishReactionResponse {

  @NonNull @Getter private final TicketType ticketType;

  protected TicketCreatorReactionResponse(
      BotResponsiveMessage message, @NonNull TicketType ticketType) {
    super(message);
    this.ticketType = ticketType;
  }

  @NonNull
  public static BotResponsiveMessage add(
      @NonNull BotResponsiveMessage responsiveMessage, Message message) {
    for (TicketType ticketType :
        Arrays.asList(
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
    if (this.message == null) return true;
    BotUser user = Starfish.getLoader().getStarfishUser(event.getUserIdLong());
    Starfish.getLoader()
        .getTicket(this.message.getData().getOr("ticket", Long.class, -1L))
        .ifPresent(
            ticket -> {
              try {
                Starfish.getTicketManager().createTicket(this.ticketType, user, ticket);
                ResponsiveMessage message =
                    Starfish.getLoader()
                        .getResponsiveMessage(event.getGuild(), event.getMessageIdLong());
                if (message instanceof BotResponsiveMessage)
                  ((BotResponsiveMessage) message).delete();
              } catch (TicketCreationException e) {
                event
                    .getTextChannel()
                    .sendMessage(e.toMessage().build())
                    .queue(Messages.getErrorConsumer());
              }
            });
    return true;
  }

  @Override
  public @NonNull String getUnicode() {
    return StarfishReactionResponse.getUnicode(
        "unicode.tickets." + this.ticketType.name().toLowerCase());
  }
}
