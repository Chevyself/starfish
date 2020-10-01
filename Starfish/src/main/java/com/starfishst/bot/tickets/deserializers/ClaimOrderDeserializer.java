package com.starfishst.bot.tickets.deserializers;

import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.data.messages.order.ClaimOrderResponsiveMessage;
import com.starfishst.bot.tickets.StarfishMessageDeserializer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/** Deserializes claim order messages */
public class ClaimOrderDeserializer
    implements StarfishMessageDeserializer<ClaimOrderResponsiveMessage> {

  @NotNull
  @Override
  public ClaimOrderResponsiveMessage getMessage(
      long id, @NotNull StarfishValuesMap data, @NotNull Document document) {
    return new ClaimOrderResponsiveMessage(id, data);
  }
}
