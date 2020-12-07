package com.starfishst.bot.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starfishst.adapters.LongMongoAdapter;
import com.starfishst.adapters.MongoObjectDeserializer;
import com.starfishst.adapters.PermissionAdapter;
import com.starfishst.adapters.PermissionStackDeserializer;
import com.starfishst.adapters.ResponsiveMessageAdapter;
import com.starfishst.adapters.ValuesMapAdapter;
import com.starfishst.api.data.messages.BotResponsiveMessage;
import com.starfishst.api.permissions.Permission;
import com.starfishst.api.permissions.PermissionStack;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.data.StarfishValuesMap;
import lombok.NonNull;

/** Static utilities for mongo */
public class Mongo {

  /**
   * Creates a gson instance for mongo
   *
   * @return the created gson instance
   */
  @NonNull
  public static Gson createGson() {
    LongMongoAdapter longAdapter = new LongMongoAdapter();
    ValuesMapAdapter valuesMapAdapter = new ValuesMapAdapter();
    return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Long.class, longAdapter)
        .registerTypeAdapter(long.class, longAdapter)
        .registerTypeAdapter(Object.class, new MongoObjectDeserializer())
        .registerTypeAdapter(Permission.class, new PermissionAdapter())
        .registerTypeAdapter(PermissionStack.class, new PermissionStackDeserializer())
        .registerTypeAdapter(ValuesMap.class, valuesMapAdapter)
        .registerTypeAdapter(BotResponsiveMessage.class, new ResponsiveMessageAdapter())
        .registerTypeAdapter(StarfishValuesMap.class, valuesMapAdapter)
        .create();
  }
}
