package com.starfishst.ethot.objects.responsive.type.ticket;

import com.starfishst.ethot.objects.responsive.ReactionResponse;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.tickets.type.TicketCreator;
import com.starfishst.simple.Lots;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    return Lots.list(
        new OrderReactionResponse(creator),
        new ApplyReactionResponse(creator),
        new SupportReactionResponse(creator));
  }
}
