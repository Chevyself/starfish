package com.starfishst.bot.objects.responsive.type.ticket;

import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.bot.oldtickets.type.TicketCreator;
import com.starfishst.bot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/** The reaction response to create an order ticket */
public class OrderReactionResponse extends TicketReactionResponse {

  /**
   * Create the reaction response
   *
   * @param creator the ticket creator waiting for the reaction
   */
  public OrderReactionResponse(@NotNull TicketCreator creator) {
    super(creator);
  }

  @Override
  protected @NotNull TicketType getType() {
    return TicketType.ORDER;
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.getEmoji("UNICODE_ORDER");
  }
}
