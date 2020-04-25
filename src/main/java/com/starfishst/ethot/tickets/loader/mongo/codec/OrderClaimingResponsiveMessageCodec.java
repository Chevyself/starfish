package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.objects.responsive.type.orders.OrderClaimingResponsiveMessage;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link OrderClaimingResponsiveMessage} for mongo */
public class OrderClaimingResponsiveMessageCodec implements Codec<OrderClaimingResponsiveMessage> {

  @Override
  public void encode(
      BsonWriter writer, OrderClaimingResponsiveMessage message, EncoderContext context) {
    writer.writeStartDocument();
    writer.writeString("type", message.getType().toString());
    writer.writeInt64("id", message.getId());
    writer.writeEndDocument();
  }

  @Override
  public Class<OrderClaimingResponsiveMessage> getEncoderClass() {
    return OrderClaimingResponsiveMessage.class;
  }

  @Override
  public OrderClaimingResponsiveMessage decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    ResponsiveMessageType type = ResponsiveMessageType.valueOf(reader.readString("type"));
    long id = reader.readInt64("id");
    reader.readEndDocument();
    if (type == ResponsiveMessageType.ORDER) {
      return new OrderClaimingResponsiveMessage(id);
    } else {
      throw new IllegalArgumentException(type + " ies not an order responsive message");
    }
  }
}
