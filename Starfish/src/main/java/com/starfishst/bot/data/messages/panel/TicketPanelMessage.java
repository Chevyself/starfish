package com.starfishst.bot.data.messages.panel;

import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import lombok.NonNull;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Message;

/** The message to create tickets */
public class TicketPanelMessage extends StarfishResponsiveMessage {
  /**
   * Create the ticket panel
   *
   * @param message the message to become the ticket panel
   */
  public TicketPanelMessage(@NonNull Message message) {
    super(
        message,
        Lots.set(new TicketPanelReactionResponse()),
        "ticket-panel",
        new StarfishValuesMap());
  }
}
