package com.starfishst.ethot.objects.responsive.type.quotes;

import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.simple.Lots;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** This is the message that is send to the channel ticket for the customer to see the offer */
public class OfferAcceptResponsiveMessage extends ResponsiveMessage {

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   */
  public OfferAcceptResponsiveMessage(@NotNull Message message) {
    super(
        ResponsiveMessageType.OFFER, message, Lots.list(new OfferAcceptReactionResponse()), false);
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param id the id of the message
   */
  public OfferAcceptResponsiveMessage(long id) {
    super(ResponsiveMessageType.OFFER, id, Lots.list(new OfferAcceptReactionResponse()), false);
  }
}
