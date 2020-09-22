package com.starfishst.bot.oldconfig.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.starfishst.bot.util.Discord;
import java.lang.reflect.Type;
import net.dv8tion.jda.api.entities.Category;

/**
 * Gson adapters convert <a href="https://www.json.org/json-en.html">Json</a> objects to java
 * objects.
 *
 * <p>This one in particular, converts {@link Category}
 */
public class CategoryAdapter implements JsonDeserializer<Category>, JsonSerializer<Category> {

  @Override
  public Category deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return Discord.getCategory(jsonElement.getAsLong());
  }

  @Override
  public JsonElement serialize(
      Category category, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(category.getIdLong());
  }
}
