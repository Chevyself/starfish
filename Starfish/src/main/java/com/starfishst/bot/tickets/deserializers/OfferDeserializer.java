package com.starfishst.bot.tickets.deserializers;

import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.data.messages.offer.OfferMessage;
import com.starfishst.bot.tickets.StarfishMessageDeserializer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/** Deserializes offers */
public class OfferDeserializer implements StarfishMessageDeserializer<OfferMessage> {

  @NotNull
  @Override
  public OfferMessage getMessage(
      long id, @NotNull StarfishValuesMap data, @NotNull Document document) {
    return new OfferMessage(id, data);
  }
}
