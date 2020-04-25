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
public class SupportReactionResponse extends TicketReactionResponse {

  public SupportReactionResponse(@NotNull TicketCreator creator) {
    super(creator);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.HAMMER;
  }

  @Override
  protected @NotNull TicketType getType() {
    return TicketType.SUPPORT;
  }
}
