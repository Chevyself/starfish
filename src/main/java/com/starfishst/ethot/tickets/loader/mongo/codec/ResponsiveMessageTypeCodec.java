package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Decodes and encodes {@link ResponsiveMessageType} for mongo usage
 *
 * @author Chevy
 * @version 1.0.0
 */
public class ResponsiveMessageTypeCodec implements Codec<ResponsiveMessageType> {

  @Override
  public ResponsiveMessageType decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return ResponsiveMessageType.valueOf(bsonReader.readString());
  }

  @Override
  public void encode(BsonWriter writer, ResponsiveMessageType type, EncoderContext encoderContext) {
    writer.writeString(type.toString());
  }

  @Override
  public Class<ResponsiveMessageType> getEncoderClass() {
    return ResponsiveMessageType.class;
  }
}
