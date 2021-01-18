package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.starfishst.api.messages.BotResponsiveMessage;
import java.lang.reflect.Type;

public class ResponsiveMessageDeserializer implements JsonDeserializer<BotResponsiveMessage> {

  @Override
  public BotResponsiveMessage deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, BotResponsiveMessage.class);
  }
}
