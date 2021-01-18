package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.starfishst.api.permissions.Permission;
import com.starfishst.bot.data.StarfishPermission;
import java.lang.reflect.Type;
import me.googas.commons.gson.adapters.JsonAdapter;

public class PermissionAdapter implements JsonAdapter<Permission> {
  @Override
  public JsonElement serialize(Permission src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.getNodeAppended());
  }

  @Override
  public Permission deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    String string = json.getAsString();
    if (string.startsWith("-")) {
      return new StarfishPermission(string.substring(1), false);
    } else {
      return new StarfishPermission(string, true);
    }
  }
}
