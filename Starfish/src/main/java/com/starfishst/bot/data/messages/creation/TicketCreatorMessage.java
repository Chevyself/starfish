package com.starfishst.bot.data.messages.creation;

import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.handlers.lang.StarfishLocaleFile;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.maps.Maps;
import java.util.HashSet;
import java.util.Set;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** This is send in a ticket creator for the user to select between selecting a ticket type */
public class TicketCreatorMessage extends StarfishResponsiveMessage {

  /**
   * Create the ticket creator message
   *
   * @param message the message that will be used the creator
   * @param ticketId the id of the ticket that is the parent
   */
  public TicketCreatorMessage(@NotNull Message message, long ticketId) {
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
  @NotNull
  private static Set<TicketCreatorReactionResponse> getReactionResponses(
      @NotNull TicketCreatorMessage message) {
    StarfishLocaleFile en = Starfish.getLanguageHandler().getFile("en");
    return Lots.set(
        new TicketCreatorReactionResponse(
            TicketType.ORDER, en.get("unicode.tickets.order"), message),
        new TicketCreatorReactionResponse(
            TicketType.APPLY, en.get("unicode.tickets.apply"), message),
        new TicketCreatorReactionResponse(
            TicketType.SUPPORT, en.get("unicode.tickets.support"), message));
  }

  @Override
  public void onRemove() {}
}
