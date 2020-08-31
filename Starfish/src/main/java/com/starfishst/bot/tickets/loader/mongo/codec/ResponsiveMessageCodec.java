package com.starfishst.bot.tickets.loader.mongo.codec;

import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.objects.responsive.ResponsiveMessageType;
import com.starfishst.bot.objects.responsive.type.orders.OrderClaimingResponsiveMessage;
import com.starfishst.bot.objects.responsive.type.quotes.OfferAcceptResponsiveMessage;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link ResponsiveMessage} for mongo */
public class ResponsiveMessageCodec implements Codec<ResponsiveMessage> {

  @Override
  public ResponsiveMessage decode(BsonReader reader, DecoderContext context) {
    reader.readStartDocument();
    ResponsiveMessageType type = ResponsiveMessageType.valueOf(reader.readString("type"));
    long id = reader.readInt64("id");
    reader.readEndDocument();
    switch (type) {
      case ORDER:
        return new OrderClaimingResponsiveMessage(id);
      case OFFER:
        return new OfferAcceptResponsiveMessage(id);
      default:
        throw new IllegalArgumentException(type + " is not a valid decoding type");
    }
  }

  @Override
  public void encode(
      BsonWriter writer, ResponsiveMessage responsiveMessage, EncoderContext context) {
    writer.writeStartDocument();
    writer.writeString("type", responsiveMessage.getType().toString());
    writer.writeInt64("id", responsiveMessage.getId());
    writer.writeEndDocument();
  }

  @Override
  public Class<ResponsiveMessage> getEncoderClass() {
    return ResponsiveMessage.class;
  }
}
