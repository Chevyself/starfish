package com.starfishst.ethot.config.objects.responsive.type.quotes;

import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;

/**
 * This is the message that is send to the channel ticket for the customer to see the offer
 *
 * @author Chevy
 * @version 1.0.0
 */
public class OfferAcceptResponsiveMessage extends ResponsiveMessage {

  public OfferAcceptResponsiveMessage(long id) {
    super(ResponsiveMessageType.OFFER, id, false, new OfferAcceptReactionResponse());
  }
}
