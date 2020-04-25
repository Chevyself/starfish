package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.tickets.TicketType;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class TicketTypeCodec implements Codec<TicketType> {

  @Override
  public TicketType decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return TicketType.valueOf(bsonReader.readString());
  }

  @Override
  public void encode(BsonWriter bsonWriter, TicketType type, EncoderContext encoderContext) {
    bsonWriter.writeString(type.toString());
  }

  @Override
  public Class<TicketType> getEncoderClass() {
    return TicketType.class;
  }
}
