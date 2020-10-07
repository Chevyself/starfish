package com.starfishst.adapters.jda;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.awt.*;
import java.lang.reflect.Type;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

/** Adapter for roles */
public class RoleAdapter extends JdaObjectAdapter<Role> {
  /**
   * Create the adapter
   *
   * @param api the api to use for the adaptation
   */
  public RoleAdapter(@NotNull JDA api) {
    super(api);
  }

  @Override
  public JsonElement serialize(
      Role role, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(role.getIdLong());
  }

  @Override
  public Role deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return api.getRoleById(jsonElement.getAsLong());
  }
}
