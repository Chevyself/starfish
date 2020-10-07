package com.starfishst.adapters.jda;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import org.jetbrains.annotations.NotNull;

public class CategoryAdapter extends JdaObjectAdapter<Category> {

  /**
   * Create the adapter
   *
   * @param api the api to use for the adaptation
   */
  public CategoryAdapter(@NotNull JDA api) {
    super(api);
  }

  @Override
  public JsonElement serialize(
      Category category, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(category.getIdLong());
  }

  @Override
  public Category deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return api.getCategoryById(jsonElement.getAsLong());
  }
}
