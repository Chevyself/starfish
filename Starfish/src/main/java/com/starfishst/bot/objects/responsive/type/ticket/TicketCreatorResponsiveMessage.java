package com.starfishst.bot.objects.responsive.type.ticket;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.objects.responsive.ResponsiveMessageType;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.bot.tickets.type.TicketCreator;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** This is the message sent in {@link TicketCreator} */
public class TicketCreatorResponsiveMessage extends ResponsiveMessage {

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   * @param creator the ticket creator that made this
   */
  public TicketCreatorResponsiveMessage(@NotNull Message message, @NotNull TicketCreator creator) {
    super(ResponsiveMessageType.TICKET_CREATOR, message, getReactions(creator), true);
  }

  /**
   * Get the reactions that will be used in this message
   *
   * @param creator the creator message that made this responsive message
   * @return the list of reactions
   */
  @NotNull
  private static List<ReactionResponse> getReactions(@NotNull TicketCreator creator) {
    List<ReactionResponse> responses = new ArrayList<>();
    List<TicketType> bans = Configuration.getInstance().getBannedTypes();
    if (!bans.contains(TicketType.ORDER)) {
      responses.add(new OrderReactionResponse(creator));
    }
    if (!bans.contains(TicketType.APPLY)) {
      responses.add(new ApplyReactionResponse(creator));
    }
    if (!bans.contains(TicketType.SUPPORT)) {
      responses.add(new SupportReactionResponse(creator));
    }
    if (!bans.contains(TicketType.REPORT)) {
      responses.add(new ReportReactionResponse(creator));
    }
    if (!bans.contains(TicketType.SUGGESTION)) {
      responses.add(new SuggestionReactionResponse(creator));
    }
    return responses;
  }
}
