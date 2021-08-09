package com.starfishst.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.starfishst.bot.handlers.questions.Question;
import com.starfishst.bot.handlers.questions.QuestionImage;
import com.starfishst.bot.handlers.questions.QuestionInformation;
import com.starfishst.bot.handlers.questions.QuestionRole;
import java.lang.reflect.Type;
import lombok.NonNull;

public class QuestionAdapter implements JsonSerializer<Question>, JsonDeserializer<Question> {

  @NonNull
  private static Question getQuestion(
      @NonNull JsonObject object,
      @NonNull String title,
      @NonNull String description,
      @NonNull String simple,
      int limit) {
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
  public Question deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    JsonElement titleElement = object.get("title");
    String title = titleElement == null ? "No title" : titleElement.getAsString();
    JsonElement descriptionElement = object.get("description");
    String description =
        descriptionElement == null ? "No description" : descriptionElement.getAsString();
    JsonElement simpleElement = object.get("simple");
    String simple =
        simpleElement == null
            ? "no_simple"
            : simpleElement.getAsString().replace(" ", "_").replace(".", "_");
    JsonElement limitElement = object.get("limit");
    int limit = limitElement == null ? 50 : limitElement.getAsInt();
    return QuestionAdapter.getQuestion(object, title, description, simple, limit);
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
