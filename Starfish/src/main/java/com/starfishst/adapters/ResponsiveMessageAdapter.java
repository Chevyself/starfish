package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.starfishst.api.data.messages.BotResponsiveMessage;
import com.starfishst.bot.data.messages.offer.OfferMessage;
import com.starfishst.bot.data.messages.order.ClaimOrderResponsiveMessage;
import com.starfishst.bot.data.messages.panel.TicketPanelMessage;
import com.starfishst.bot.data.messages.rating.ReviewFreelancer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import me.googas.commons.gson.adapters.JsonAdapter;
import org.jetbrains.annotations.NotNull;

public class ResponsiveMessageAdapter implements JsonAdapter<BotResponsiveMessage> {

  @NotNull private final Map<String, Class<? extends BotResponsiveMessage>> types = new HashMap<>();

  public ResponsiveMessageAdapter() {
    types.put("claim-order", ClaimOrderResponsiveMessage.class);
    types.put("offer", OfferMessage.class);
    types.put("review", ReviewFreelancer.class);
    types.put("ticket-panel", TicketPanelMessage.class);
  }

  @Override
  public JsonElement serialize(
      BotResponsiveMessage src, Type typeOfSrc, JsonSerializationContext context) {
    JsonElement element = context.serialize(src);
    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      JsonElement type = object.get("type");
      if (type == null) {
        object.addProperty("type", src.getType());
      }
    }
    return element;
  }

  @Override
  public BotResponsiveMessage deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject object = json.getAsJsonObject();
      JsonElement typeElem = object.get("type");
      if (typeElem != null) {
        String type = typeElem.getAsString();
        return context.deserialize(json, this.types.get(type));
      }
    }
    return null;
  }
}
