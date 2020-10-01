package com.starfishst.bot.data.messages.panel;

import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.core.utils.Lots;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** The message to create tickets */
public class TicketPanelMessage extends StarfishResponsiveMessage {

  /**
   * Create the ticket panel
   *
   * @param id the id of the message
   */
  public TicketPanelMessage(long id) {
    super(id, Lots.set(new TicketPanelReactionResponse()), "ticket-panel", new StarfishValuesMap());
  }

  /**
   * Create the ticket panel
   *
   * @param message the message to become the ticket panel
   */
  public TicketPanelMessage(@NotNull Message message) {
    super(
        message,
        Lots.set(new TicketPanelReactionResponse()),
        "ticket-panel",
        new StarfishValuesMap());
  }
}
