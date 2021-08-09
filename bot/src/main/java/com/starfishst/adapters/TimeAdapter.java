package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import me.googas.starbox.time.Time;

public class TimeAdapter implements JsonDeserializer<Time>, JsonSerializer<Time> {
  @Override
  public JsonElement serialize(Time src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.toString());
  }

  @Override
  public Time deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    try {
      return Time.parse(json.getAsString(), true);
    } catch (IllegalArgumentException e) {
      throw new JsonParseException(e);
    }
  }
}
