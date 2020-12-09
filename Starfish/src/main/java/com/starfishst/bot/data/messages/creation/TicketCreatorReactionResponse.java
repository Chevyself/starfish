package com.starfishst.bot.data.messages.creation;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.utility.Messages;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The ticket creator reaction response */
public class TicketCreatorReactionResponse implements ReactionResponse {

  /** The ticket type to create */
  @NonNull @Getter private final TicketType type;

  /** The unicode of the reaction */
  @NonNull private final String unicode;

  /** The ticket creator to get the message from */
  @NonNull private final TicketCreatorMessage message;

  /**
   * Create the reaction response
   *
   * @param type the type that will create this response
   * @param unicode the unicode that will be used in this response
   * @param message the message that is using this response
   */
  public TicketCreatorReactionResponse(
      @NonNull TicketType type, @NonNull String unicode, @NonNull TicketCreatorMessage message) {
    this.type = type;
    this.unicode = unicode;
    this.message = message;
  }

  @Override
  public boolean removeReaction() {
    return false;
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    BotUser user = Starfish.getLoader().getStarfishUser(event.getUserIdLong());
    Ticket ticket =
        Starfish.getLoader().getTicket(message.getData().getValueOr("id", Long.class, -1L));
    if (ticket != null) {
      try {
        Starfish.getTicketManager().createTicket(type, user, ticket);
        ticket.unload(false);
        message.unload(false);

      } catch (TicketCreationException e) {
        e.toQuery(user).send(event.getTextChannel(), Messages.getErrorConsumer());
      }
    }
    return true;
  }

  @Override
  public @NonNull String getUnicode() {
    return this.unicode;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof TicketCreatorReactionResponse)) return false;

    TicketCreatorReactionResponse that = (TicketCreatorReactionResponse) object;

    if (type != that.type) return false;
    return unicode.equals(that.unicode);
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + unicode.hashCode();
    return result;
  }
}
