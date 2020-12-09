package com.starfishst.bot.data.messages.creation;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.handlers.ticket.TicketHandler;
import java.util.HashSet;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Message;

/** This is send in a ticket creator for the user to select between selecting a ticket type */
public class TicketCreatorMessage extends StarfishResponsiveMessage {

  /**
   * Create the ticket creator message
   *
   * @param message the message that will be used the creator
   * @param ticketId the id of the ticket that is the parent
   */
  public TicketCreatorMessage(@NonNull Message message, long ticketId) {
    super(
        message,
        new HashSet<>(),
        "ticket-creator",
        new StarfishValuesMap(Maps.singleton("id", ticketId)));
    for (TicketCreatorReactionResponse response : TicketCreatorMessage.getReactionResponses(this)) {
      this.addReactionResponse(response, message);
    }
  }

  /**
   * Get the reaction responses that will be used in the creator
   *
   * @param message the message that needs the reactions
   * @return the set of reaction responses
   */
  @NonNull
  private static List<TicketCreatorReactionResponse> getReactionResponses(
      @NonNull TicketCreatorMessage message) {
    LocaleFile en = Starfish.getLanguageHandler().getFile("en");
    List<TicketCreatorReactionResponse> messages =
        Lots.list(
            new TicketCreatorReactionResponse(
                TicketType.ORDER, en.get("unicode.tickets.order"), message),
            new TicketCreatorReactionResponse(
                TicketType.APPLY, en.get("unicode.tickets.apply"), message),
            new TicketCreatorReactionResponse(
                TicketType.SUPPORT, en.get("unicode.tickets.support"), message),
            new TicketCreatorReactionResponse(
                TicketType.REPORT, en.get("unicode.tickets.report"), message),
            new TicketCreatorReactionResponse(
                TicketType.SUGGESTION, en.get("unicode.tickets.suggestion"), message));
    messages.removeIf(
        msg -> Starfish.getHandler(TicketHandler.class).getBannedTypes().contains(msg.getType()));
    return messages;
  }

  @Override
  public void onRemove() {}
}
