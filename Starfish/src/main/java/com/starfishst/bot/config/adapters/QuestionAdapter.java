package com.starfishst.bot.config.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.starfishst.bot.objects.questions.Question;
import com.starfishst.bot.objects.questions.QuestionImage;
import com.starfishst.bot.objects.questions.QuestionInformation;
import com.starfishst.bot.objects.questions.QuestionRole;
import com.starfishst.core.utils.Validate;
import java.lang.reflect.Type;

/**
 * Gson adapters convert <a href="https://www.json.org/json-en.html">Json</a> objects to java
 * objects.
 *
 * <p>This one in particular, converts {@link Question}
 *
 * @author Chevy
 * @version 1.0.0
 */
public class QuestionAdapter implements JsonDeserializer<Question>, JsonSerializer<Question> {

  @Override
  public Question deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();

    String title =
        Validate.notNullOr(
                object.get("title"),
                new JsonPrimitive("No title"),
                "Questions must have a title. In: " + object)
            .getAsString();
    String description =
        Validate.notNullOr(
                object.get("description"),
                new JsonPrimitive("No description"),
                "Questions must have a description. In: " + object)
            .getAsString();
    String simple =
        Validate.notNullOr(
                object.get("simple"),
                new JsonPrimitive("no_simple"),
                "Questions must have a simple. In: " + object)
            .getAsString();
    int limit =
        Validate.notNullOr(
                object.get("limit"),
                new JsonPrimitive(50),
                "Questions must have a limit. In: " + object)
            .getAsInt();

    if (simple.toLowerCase().startsWith("info")) {
      return new QuestionInformation(title, simple, description, limit);
    } else if (simple.toLowerCase().startsWith("image")) {
      return new QuestionImage(title, simple, description, limit);
    } else if (object.get("role") != null) {
      return new QuestionRole(title, simple, description, object.get("role").getAsString(), limit);
    } else {
      return new Question(title, simple, description, limit);
    }
  }

  @Override
  public JsonElement serialize(
      Question question, Type type, JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("title", question.getTitle());
    object.addProperty("simple", question.getSimple());
    object.addProperty("description", question.getDescription());
    object.addProperty("limit", question.getLimit());
    if (question instanceof QuestionRole) {
      object.addProperty("role", ((QuestionRole) question).getRole());
    }
    return object;
  }
}
