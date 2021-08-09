package com.starfishst.adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.starfishst.api.utility.ValuesMap;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

/** Adapts values map */
public class ValuesMapAdapter implements JsonSerializer<ValuesMap>, JsonDeserializer<ValuesMap> {

  /**
   * Objects in mongo have a prefix when its not a json primitive that's why this contains the given
   * mongo name and the type to deserialize
   */
  @NonNull
  static final Map<String, Type> objectId =
      Collections.singletonMap("numberLong", Long.class);

  /**
   * Add the values from a json object to a guido values map
   *
   * @param context the context of the object being deserialize
   * @param map the map to append the values to
   * @param object the object to get the values from
   */
  static void appendValues(
      @NonNull JsonDeserializationContext context,
      @NonNull ValuesMap map,
      @NonNull JsonObject object) {
    for (String key : object.keySet()) {
      JsonElement value = object.get(key);
      if (value.isJsonObject()) {
        JsonObject valueObject = value.getAsJsonObject();
        ArrayList<String> values = new ArrayList<>(valueObject.keySet());
        if (values.size() == 1 && values.get(0).startsWith("$")) {
          map.add(key, ValuesMapAdapter.deserialize(valueObject, values, context));
          continue;
        }
      } else if (value.isJsonArray()) {
        JsonArray array = value.getAsJsonArray();
        List<Object> objects = new ArrayList<>();
        for (JsonElement element : array) {
          if (element.isJsonObject()) {
            JsonObject elemObject = element.getAsJsonObject();
            ArrayList<String> values = new ArrayList<>(elemObject.keySet());
            if (values.size() == 1 && values.get(0).startsWith("$")) {
              objects.add(ValuesMapAdapter.deserialize(elemObject, values, context));
              continue;
            }
          }
          objects.add(context.deserialize(element, Object.class));
        }
        map.add(key, objects);
        continue;
      }
      map.add(key, context.deserialize(object.get(key), Object.class));
    }
  }

  public static Object deserialize(
      @NonNull JsonObject object,
      @NonNull List<String> values,
      @NonNull JsonDeserializationContext context) {
    if (values.size() == 1 && values.get(0).startsWith("$")) {
      Type type = ValuesMapAdapter.objectId.get(values.get(0).substring(1));
      if (type != null) {
        return context.deserialize(object.get(values.get(0)), type);
      } else {
        return context.deserialize(object.get(values.get(0)), Object.class);
      }
    }
    return null;
  }

  @Override
  public JsonElement serialize(ValuesMap src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src.getMap());
  }

  @Override
  public ValuesMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    ValuesMap map = new ValuesMap(new LinkedHashMap<>());
    ValuesMapAdapter.appendValues(context, map, json.getAsJsonObject());
    return map;
  }
}
