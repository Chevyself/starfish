package com.starfishst.bot.oldconfig.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.awt.*;
import java.lang.reflect.Type;

/**
 * Gson adapters convert <a href="https://www.json.org/json-en.html">Json</a> objects to java
 * objects.
 *
 * <p>This one in particular, converts {@link Color}
 *
 * @author Chevy
 * @version 1.0.0
 */
public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

  @Override
  public Color deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return new Color(Integer.decode(jsonElement.getAsString()));
  }

  @Override
  public JsonElement serialize(
      Color color, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(
        String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
  }
}
