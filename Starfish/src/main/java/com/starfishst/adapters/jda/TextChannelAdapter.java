package com.starfishst.adapters.jda;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class TextChannelAdapter extends JdaObjectAdapter<TextChannel> {

  /**
   * Create the adapter
   *
   * @param api the api to use for the adaptation
   */
  public TextChannelAdapter(@NotNull JDA api) {
    super(api);
  }

  @Override
  public JsonElement serialize(
      TextChannel channel, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(channel.getIdLong());
  }

  @Override
  public TextChannel deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return api.getTextChannelById(jsonElement.getAsLong());
  }
}
