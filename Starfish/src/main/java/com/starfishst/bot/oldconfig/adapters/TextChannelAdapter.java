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
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Gson adapters convert <a href="https://www.json.org/json-en.html">Json</a> objects to java
 * objects.
 *
 * <p>This one in particular, converts {@link TextChannel}
 *
 * @author Chevy
 * @version 1.0.0
 */
public class TextChannelAdapter
    implements JsonSerializer<TextChannel>, JsonDeserializer<TextChannel> {

  @Override
  public TextChannel deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return Discord.getTextChannel(jsonElement.getAsLong());
  }

  @Override
  public JsonElement serialize(
      TextChannel channel, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(channel.getIdLong());
  }
}
