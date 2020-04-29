package com.starfishst.ethot.objects.responsive.type.orders;

import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.simple.Lots;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/**
 * This represents the embed message sent when an order is announced and a freelancer can claim it
 */
public class OrderClaimingResponsiveMessage extends ResponsiveMessage {

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   */
  public OrderClaimingResponsiveMessage(@NotNull Message message) {
    super(
        ResponsiveMessageType.ORDER,
        message,
        Lots.list(new OrderClaimingReactionResponse()),
        false);
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param id the id of the message
   */
  public OrderClaimingResponsiveMessage(long id) {
    super(ResponsiveMessageType.ORDER, id, Lots.list(new OrderClaimingReactionResponse()), false);
  }
}
