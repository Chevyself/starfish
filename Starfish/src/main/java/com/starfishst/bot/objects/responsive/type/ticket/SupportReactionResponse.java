package com.starfishst.bot.objects.responsive.type.ticket;

import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.bot.oldtickets.type.TicketCreator;
import com.starfishst.bot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/** The reaction response to create a support ticket */
public class SupportReactionResponse extends TicketReactionResponse {

  /**
   * Create the reaction response
   *
   * @param creator the ticket creator waiting for the reaction
   */
  public SupportReactionResponse(@NotNull TicketCreator creator) {
    super(creator);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.getEmoji("UNICODE_SUPPORT");
  }

  @Override
  protected @NotNull TicketType getType() {
    return TicketType.SUPPORT;
  }
}
