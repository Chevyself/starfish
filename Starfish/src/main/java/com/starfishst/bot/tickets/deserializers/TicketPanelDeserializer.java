package com.starfishst.bot.tickets.deserializers;

import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.data.messages.panel.TicketPanelMessage;
import com.starfishst.bot.tickets.StarfishMessageDeserializer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/** Deserializes ticket panel messages */
public class TicketPanelDeserializer implements StarfishMessageDeserializer<TicketPanelMessage> {

  @NotNull
  @Override
  public TicketPanelMessage getMessage(
      long id, @NotNull StarfishValuesMap data, @NotNull Document document) {
    return new TicketPanelMessage(id);
  }
}
