package com.starfishst.bot.tickets.loader.mongo.codec;

import com.starfishst.bot.objects.responsive.ResponsiveMessageType;
import com.starfishst.bot.objects.responsive.type.product.ProductShopResponsiveMessage;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link ProductShopResponsiveMessage} for mongo */
public class ProductShopResponsiveMessageCodec implements Codec<ProductShopResponsiveMessage> {

  @Override
  public void encode(
      BsonWriter writer, ProductShopResponsiveMessage message, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString("type", message.getType().toString());
    writer.writeInt64("id", message.getId());
    writer.writeEndDocument();
  }

  @Override
  public Class<ProductShopResponsiveMessage> getEncoderClass() {
    return ProductShopResponsiveMessage.class;
  }

  @Override
  public ProductShopResponsiveMessage decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    ResponsiveMessageType type = ResponsiveMessageType.valueOf(reader.readString("type"));
    long id = reader.readInt64("id");
    reader.readEndDocument();
    if (type == ResponsiveMessageType.PRODUCT) {
      return new ProductShopResponsiveMessage(id);
    } else {
      throw new IllegalArgumentException(type + " ies not an product shop responsive message");
    }
  }
}
