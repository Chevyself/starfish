package com.starfishst.bot.objects.responsive.type.ticket;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.tickets.type.TicketCreator;
import org.jetbrains.annotations.NotNull;

/** The reaction response to create an apply ticket */
public class ApplyReactionResponse extends TicketReactionResponse {

  /**
   * Create the reaction response
   *
   * @param creator the ticket creator waiting for the reaction
   */
  public ApplyReactionResponse(@NotNull TicketCreator creator) {
    super(creator);
  }

  @Override
  public @NotNull String getUnicode() {
    return Lang.get("UNICODE_APPLY");
  }

  @Override
  protected @NotNull TicketType getType() {
    return TicketType.APPLY;
  }
}
