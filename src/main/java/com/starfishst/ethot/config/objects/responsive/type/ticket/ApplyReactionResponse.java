package com.starfishst.ethot.config.objects.responsive.type.ticket;

import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.TicketCreator;
import com.starfishst.ethot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/**
 * When :pencil: is reacted this will be executed
 *
 * @author Chevy
 * @version 1.0
 */
public class ApplyReactionResponse extends TicketReactionResponse {

  public ApplyReactionResponse(@NotNull TicketCreator creator) {
    super(creator);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.MEMO;
  }

  @Override
  protected @NotNull TicketType getType() {
    return TicketType.APPLY;
  }
}
