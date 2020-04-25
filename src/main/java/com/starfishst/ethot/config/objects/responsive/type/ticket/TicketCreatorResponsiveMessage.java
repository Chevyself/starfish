package com.starfishst.ethot.config.objects.responsive.type.ticket;

import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.tickets.type.TicketCreator;
import org.jetbrains.annotations.NotNull;

/**
 * This is the message sent in {@link TicketCreator}
 *
 * <ul>
 *   <li>:white_check_mark: order
 *   <li>:pencil: apply
 *   <li>:gear: support
 * </ul>
 *
 * @author Chevy
 * @version 1.0.0
 */
public class TicketCreatorResponsiveMessage extends ResponsiveMessage {

  public TicketCreatorResponsiveMessage(long id, @NotNull TicketCreator ticketCreator) {
    super(
        ResponsiveMessageType.TICKET,
        id,
        true,
        new OrderReactionResponse(ticketCreator),
        new ApplyReactionResponse(ticketCreator),
        new SupportReactionResponse(ticketCreator));
  }
}
