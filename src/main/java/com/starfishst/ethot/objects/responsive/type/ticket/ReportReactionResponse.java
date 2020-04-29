package com.starfishst.ethot.objects.responsive.type.ticket;

import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.TicketCreator;
import com.starfishst.ethot.util.Unicode;
import org.jetbrains.annotations.NotNull;

/** The reaction to create a report */
public class ReportReactionResponse extends TicketReactionResponse {

  /**
   * Create the reaction response
   *
   * @param creator the ticket creator waiting for the reaction
   */
  public ReportReactionResponse(@NotNull TicketCreator creator) {
    super(creator);
  }

  @Override
  public @NotNull String getUnicode() {
    return Unicode.getEmoji("UNICODE_REPORT");
  }

  @Override
  protected @NotNull TicketType getType() {
    return TicketType.REPORT;
  }
}
