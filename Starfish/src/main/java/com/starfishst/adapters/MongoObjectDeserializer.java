package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MongoObjectDeserializer implements JsonDeserializer<Object> {

  @Override
  public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      if (json.isJsonObject()) {
        JsonObject valueObject = json.getAsJsonObject();
        List<String> values = new ArrayList<>(valueObject.keySet());
        if (values.size() == 1 && values.get(0).startsWith("$")) {
          return ValuesMapAdapter.deserialize(valueObject, values, context);
        }
      }
    }
    return null;
  }
}
