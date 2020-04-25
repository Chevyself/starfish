package com.starfishst.ethot.config.objects.responsive.type.ticket;

import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.TicketCreator;
import com.starfishst.ethot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/**
 * When :gear: is reacted this will be executed
 *
 * @author Chevy
 * @version 1.0
 */
public class OrderReactionResponse extends TicketReactionResponse {

  public OrderReactionResponse(@NotNull TicketCreator creator) {
    super(creator);
  }

  @Override
  protected @NotNull TicketType getType() {
    return TicketType.ORDER;
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.WHITE_CHECK_MARK;
  }
}
