package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.starfishst.api.messages.BotReactionResponse;
import com.starfishst.bot.messages.ClaimOrderReactionResponse;
import com.starfishst.bot.messages.OfferAcceptReactionResponse;
import com.starfishst.bot.messages.ReviewReactionResponse;
import com.starfishst.bot.messages.TicketCreatorReactionResponse;
import com.starfishst.bot.messages.TicketPanelReactionResponse;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commands.jda.utils.responsive.ReactionResponse;

public class ReactionResponseAdapter
    implements JsonSerializer<ReactionResponse>, JsonDeserializer<ReactionResponse> {

  @NonNull @Getter
  private static final Map<String, Class<? extends ReactionResponse>> map = new HashMap<>();

  static {
    ReactionResponseAdapter.map.put("claim-order", ClaimOrderReactionResponse.class);
    ReactionResponseAdapter.map.put("offer", OfferAcceptReactionResponse.class);
    ReactionResponseAdapter.map.put("review", ReviewReactionResponse.class);
    ReactionResponseAdapter.map.put("ticket-creator", TicketCreatorReactionResponse.class);
    ReactionResponseAdapter.map.put("ticket-panel", TicketPanelReactionResponse.class);
  }

  @Override
  public JsonElement serialize(
      ReactionResponse src, Type typeOfSrc, JsonSerializationContext context) {
    if (!(src instanceof BotReactionResponse)) return null;
    Class<? extends ReactionResponse> aClass =
        ReactionResponseAdapter.map.get(((BotReactionResponse) src).getType());
    if (aClass == null) return null;
    JsonElement serialize = context.serialize(src, aClass);
    if (serialize.isJsonObject()) {
      JsonObject object = serialize.getAsJsonObject();
      if (object.get("type") == null) {
        object.addProperty("type", ((BotReactionResponse) src).getType());
      }
    }
    return serialize;
  }

  @Override
  public ReactionResponse deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (!json.isJsonObject()) return null;
    JsonObject jsonObject = json.getAsJsonObject();
    if (jsonObject.get("type") == null) return null;
    String type = jsonObject.get("type").getAsString();
    if (type == null) return null;
    Class<? extends ReactionResponse> aClass = ReactionResponseAdapter.map.get(type);
    if (aClass == null) return null;
    return context.deserialize(json, aClass);
  }
}
