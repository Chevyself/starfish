package com.starfishst.bot.config.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.starfishst.bot.objects.invoicing.Fee;
import java.lang.reflect.Type;

/** Serializes {@link Fee} */
public class FeeAdapter implements JsonSerializer<Fee>, JsonDeserializer<Fee> {

  @Override
  public JsonElement serialize(Fee fee, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("description", fee.getDescription());
    object.addProperty("percentage", fee.getPercentage());
    object.addProperty("min", fee.getMin());
    object.addProperty("max", fee.getMax());
    return object;
  }

  @Override
  public Fee deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    String description;
    if (object.get("description") == null) {
      throw new JsonParseException("Description cannot be null! in: " + json);
    } else {
      description = object.get("description").getAsString();
    }
    double percentage =
        object.get("percentage") == null ? 0 : object.get("percentage").getAsDouble();
    double addition = object.get("addition") == null ? 0 : object.get("addition").getAsDouble();
    double min = object.get("min") == null ? 0 : object.get("min").getAsDouble();
    double max = object.get("max") == null ? 0 : object.get("max").getAsDouble();
    return new Fee(description, percentage, addition, min, max);
  }
}
