package com.starfishst.ethot.config.objects.responsive.type.orders;

import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;

/**
 * This represents the embed message sent when an order is announced and a freelancer can claim it
 *
 * @author Chevy
 * @version 1.0.0
 */
public class OrderClaimingResponsiveMessage extends ResponsiveMessage {

  public OrderClaimingResponsiveMessage(long id) {
    super(ResponsiveMessageType.ORDER, id, false, new OrderClaimingReactionResponse());
  }
}
