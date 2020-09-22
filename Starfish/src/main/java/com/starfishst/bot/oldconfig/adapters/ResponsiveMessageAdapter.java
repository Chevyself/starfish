package com.starfishst.bot.oldconfig.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.objects.responsive.ResponsiveMessageType;
import com.starfishst.bot.objects.responsive.type.archive.ArchiveResponsiveMessage;
import com.starfishst.bot.objects.responsive.type.freelancer.ReviewFreelancer;
import com.starfishst.bot.objects.responsive.type.inactive.InactiveCheckResponsiveMessage;
import com.starfishst.bot.objects.responsive.type.panel.TicketPanel;
import com.starfishst.bot.objects.responsive.type.unicode.UnicodeGiverResponsiveMessage;
import com.starfishst.bot.objects.responsive.type.verification.VerificationResponsiveMessage;
import com.starfishst.simple.Lots;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Gson adapters convert <a href="https://www.json.org/json-en.html">Json</a> objects to java
 * objects.
 *
 * <p>This one in particular, converts {@link Color}
 */
public class ResponsiveMessageAdapter
    implements JsonDeserializer<ResponsiveMessage>, JsonSerializer<ResponsiveMessage> {

  @Override
  public ResponsiveMessage deserialize(
      JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    ResponsiveMessageType type = ResponsiveMessageType.valueOf(object.get("type").getAsString());
    long id = object.get("id").getAsLong();
    switch (type) {
      case REVIEW:
        long freelancer = object.get("freelancer").getAsLong();
        long user = object.get("user").getAsLong();
        return new ReviewFreelancer(id, freelancer, user);
      case ARCHIVE:
        return new ArchiveResponsiveMessage(id);
      case INACTIVE_CHECK:
        long createdAt = object.get("createdAt").getAsLong();
        long ticket = object.get("ticket").getAsLong();
        boolean finished = object.get("finished").getAsBoolean();
        List<Long> reacted = Lots.list(context.deserialize(object.get("reacted"), Long[].class));
        return new InactiveCheckResponsiveMessage(id, createdAt, ticket, reacted, finished);
      case JOIN_VERIFICATION:
        return new VerificationResponsiveMessage(id);
      case TICKET_PANEL:
        return new TicketPanel(id);
      case UNICODE_GIVER:
        return new UnicodeGiverResponsiveMessage(id);
    }
    return null;
  }

  @Override
  public JsonElement serialize(
      ResponsiveMessage responsiveMessage, Type type, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("type", responsiveMessage.getType().toString());
    object.addProperty("id", responsiveMessage.getId());
    switch (responsiveMessage.getType()) {
      case REVIEW:
        object.addProperty("freelancer", ((ReviewFreelancer) responsiveMessage).getFreelancer());
        object.addProperty("user", ((ReviewFreelancer) responsiveMessage).getUser());
        break;
      case INACTIVE_CHECK:
        InactiveCheckResponsiveMessage checkResponsiveMessage =
            (InactiveCheckResponsiveMessage) responsiveMessage;
        object.addProperty("createdAt", checkResponsiveMessage.getCreatedAt());
        object.addProperty("finished", checkResponsiveMessage.isFinished());
        object.addProperty("ticket", checkResponsiveMessage.getTicketId());
        object.add("reacted", context.serialize(checkResponsiveMessage.getReacted()));

        break;
    }
    return object;
  }
}
