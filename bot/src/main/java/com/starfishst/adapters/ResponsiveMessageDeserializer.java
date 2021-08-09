package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.utility.ValuesMap;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import me.googas.commands.jda.utils.responsive.ReactionResponse;

public class ResponsiveMessageDeserializer implements JsonDeserializer<BotResponsiveMessage> {

  @Override
  public BotResponsiveMessage deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (!json.isJsonObject()) return null;
    JsonObject object = json.getAsJsonObject();
    if (object.get("id") == null) return null;
    if (object.get("reactions") == null) return null;
    if (object.get("data") == null) return null;
    BotResponsiveMessage message =
        new BotResponsiveMessage(
            (Long) context.deserialize(object.get("id"), Long.class),
            // TODO fix
            new HashSet<>(
                Arrays.asList(
                    context.deserialize(object.get("reactions"), ReactionResponse[].class))),
            context.deserialize(object.get("data"), ValuesMap.class));
    message.update();
    return message;
  }
}
