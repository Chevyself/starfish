package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/** The adapter for mongo longs */
public class LongMongoAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {

  @Override
  public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src);
  }

  @Override
  public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject object = json.getAsJsonObject();
      try {
        return Long.valueOf(object.get("$numberLong").getAsString());
      } catch (NumberFormatException e) {
        return -1L;
      }
    } else {
      return json.getAsLong();
    }
  }
}
