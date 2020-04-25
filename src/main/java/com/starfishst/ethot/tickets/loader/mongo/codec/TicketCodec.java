package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.freelancers.Offer;
import com.starfishst.ethot.objects.questions.Answer;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.type.orders.OrderClaimingResponsiveMessage;
import com.starfishst.ethot.objects.responsive.type.product.ProductShopResponsiveMessage;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.loader.mongo.MongoTicketLoader;
import com.starfishst.ethot.tickets.type.Apply;
import com.starfishst.ethot.tickets.type.CheckOut;
import com.starfishst.ethot.tickets.type.Order;
import com.starfishst.ethot.tickets.type.Product;
import com.starfishst.ethot.tickets.type.Quote;
import com.starfishst.ethot.tickets.type.Support;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.tickets.type.TicketCreator;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.TextChannelImpl;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Decode and encode {@link Ticket} for mongo */
public class TicketCodec implements Codec<Ticket> {

  @Override
  public Ticket decode(BsonReader reader, DecoderContext context) {
    CodecRegistry codecRegistry = MongoTicketLoader.getCodecRegistry();

    long id = -1;
    long parentId = -1;
    User user = null;
    TicketStatus status = null;
    TicketType type = null;
    TextChannel channel = null;
    HashMap<String, Answer> details = new HashMap<>();
    Freelancer freelancer = null;
    ResponsiveMessage message = null;
    List<Offer> offers = new ArrayList<>();
    reader.readStartDocument();
    reader.readObjectId();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      if (fieldName.equalsIgnoreCase("id")) {
        id = reader.readInt64();
      } else if (fieldName.equalsIgnoreCase("user")) {
        user = context.decodeWithChildContext(codecRegistry.get(UserImpl.class), reader);
      } else if (fieldName.equalsIgnoreCase("status")) {
        status = context.decodeWithChildContext(codecRegistry.get(TicketStatus.class), reader);
      } else if (fieldName.equalsIgnoreCase("type")) {
        type = context.decodeWithChildContext(codecRegistry.get(TicketType.class), reader);
      } else if (fieldName.equalsIgnoreCase("channel")) {
        channel = context.decodeWithChildContext(codecRegistry.get(TextChannelImpl.class), reader);
      } else if (fieldName.equalsIgnoreCase("details")) {
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
          String simple = reader.readName();
          details.put(
              simple, context.decodeWithChildContext(codecRegistry.get(Answer.class), reader));
        }
        reader.readEndDocument();
      } else if (fieldName.equalsIgnoreCase("freelancer")) {
        freelancer = TicketManager.getInstance().getLoader().getFreelancer(reader.readInt64());
      } else if (fieldName.equalsIgnoreCase("message")) {
        if (type != null) {
          switch (type) {
            case ORDER:
              message =
                  context.decodeWithChildContext(
                      codecRegistry.get(OrderClaimingResponsiveMessage.class), reader);
              break;
            case PRODUCT:
              message =
                  context.decodeWithChildContext(
                      codecRegistry.get(ProductShopResponsiveMessage.class), reader);
          }
        }
      } else if (fieldName.equalsIgnoreCase("offers")) {
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
          offers.add(
              context.decodeWithChildContext(
                  MongoTicketLoader.getCodecRegistry().get(Offer.class), reader));
        }
        reader.readEndArray();
      } else if (fieldName.equalsIgnoreCase("parent")) {
        parentId = reader.readInt64();
      }
    }
    reader.readEndDocument();
    if (id == -1 | status == null | type == null) {
      throw new IllegalArgumentException("Status and Type cannot be null! Or incorrect id!");
    }
    switch (type) {
      case ORDER:
        return new Order(
            id,
            user,
            status,
            channel,
            details,
            freelancer,
            (OrderClaimingResponsiveMessage) message);
      case SUPPORT:
        return new Support(id, user, status, channel, details);
      case APPLY:
        return new Apply(id, user, status, channel, details);
      case QUOTE:
        return new Quote(id, user, status, channel, details, freelancer, offers);
      case TICKET_CREATOR:
        return new TicketCreator(id, user, channel);
      case PRODUCT:
        return new Product(
            id, user, status, channel, details, (ProductShopResponsiveMessage) message);
      case CHECK_OUT:
        return new CheckOut(id, user, status, channel, freelancer, parentId);
      default:
        throw new IllegalArgumentException(type + " is not a valid type");
    }
  }

  @Override
  public void encode(BsonWriter bsonWriter, Ticket ticket, EncoderContext encoderContext) {
    MongoTicketLoader.getCodecRegistry()
        .get(Document.class)
        .encode(bsonWriter, ticket.getDocument(), encoderContext);
  }

  @Override
  public Class<Ticket> getEncoderClass() {
    return Ticket.class;
  }
}
