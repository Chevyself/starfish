package com.starfishst.bot.data.messages.creation;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.utils.responsive.ReactionResponse;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The ticket creator reaction response */
public class TicketCreatorReactionResponse implements ReactionResponse {

  /** The ticket type to create */
  @NotNull private final TicketType type;

  /** The unicode of the reaction */
  @NotNull private final String unicode;

  /** The ticket creator to get the message from */
  @NotNull private final TicketCreatorMessage message;

  /**
   * Create the reaction response
   *
   * @param type the type that will create this response
   * @param unicode the unicode that will be used in this response
   * @param message the message that is using this response
   */
  public TicketCreatorReactionResponse(
      @NotNull TicketType type, @NotNull String unicode, @NotNull TicketCreatorMessage message) {
    this.type = type;
    this.unicode = unicode;
    this.message = message;
  }

  @Override
  public boolean removeReaction() {
    return false;
  }

  @Override
  public void onReaction(@NotNull MessageReactionAddEvent event) {
    BotUser user = Starfish.getLoader().getStarfishUser(event.getUserIdLong());
    Ticket ticket =
        Starfish.getLoader().getTicket(message.getData().getValueOr("id", Long.class, -1L));
    if (ticket != null) {
      try {
        Starfish.getTicketManager().createTicket(type, user, ticket);
      } catch (TicketCreationException e) {
        e.toQuery(user).send(event.getTextChannel());
      }
      ticket.unload(false);
    }
    message.unload(false);
  }

  @Override
  public @NotNull String getUnicode() {
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
