package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.starfishst.api.permissions.PermissionStack;
import com.starfishst.bot.tickets.StarfishPermissionStack;
import java.lang.reflect.Type;
import me.googas.commons.gson.adapters.JsonAdapter;

public class PermissionStackDeserializer implements JsonAdapter<PermissionStack> {

  @Override
  public JsonElement serialize(
      PermissionStack src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src, StarfishPermissionStack.class);
  }

  @Override
  public PermissionStack deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, StarfishPermissionStack.class);
  }
}
