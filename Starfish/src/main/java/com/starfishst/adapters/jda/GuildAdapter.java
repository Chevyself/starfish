package com.starfishst.adapters.jda;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public class GuildAdapter extends JdaObjectAdapter<Guild> {

  /**
   * Create the adapter
   *
   * @param api the api to use for the adaptation
   */
  public GuildAdapter(@NotNull JDA api) {
    super(api);
  }

  @Override
  public JsonElement serialize(
      Guild guild, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(guild.getIdLong());
  }

  @Override
  public Guild deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return api.getGuildById(jsonElement.getAsLong());
  }
}
