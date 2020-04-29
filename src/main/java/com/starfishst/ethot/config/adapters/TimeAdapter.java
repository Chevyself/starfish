package com.starfishst.ethot.config.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.time.Time;
import com.starfishst.core.utils.time.Unit;
import java.lang.reflect.Type;

/**
 * Gson adapters convert <a href="https://www.json.org/json-en.html">Json</a> objects to java
 * objects.
 *
 * <p>This one in particular, converts {@link Time}
 */
public class TimeAdapter implements JsonSerializer<Time>, JsonDeserializer<Time> {

  @Override
  public Time deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    String element = jsonElement.getAsString();
    try {
      return Time.fromString(element);
    } catch (IllegalArgumentException e) {
      Errors.addError(element + " is not the correct format of time: 1d, 2h, 4w is");
      return new Time(10, Unit.MINUTES);
    }
  }

  @Override
  public JsonElement serialize(
      Time time, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(time.toDatabaseString());
  }
}
