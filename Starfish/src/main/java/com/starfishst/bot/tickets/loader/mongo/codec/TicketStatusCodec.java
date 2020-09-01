package com.starfishst.bot.tickets.loader.mongo.codec;

import com.starfishst.api.tickets.TicketStatus;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link TicketStatus} for mongo */
public class TicketStatusCodec implements Codec<TicketStatus> {

  @Override
  public TicketStatus decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return TicketStatus.valueOf(bsonReader.readString());
  }

  @Override
  public void encode(BsonWriter bsonWriter, TicketStatus status, EncoderContext encoderContext) {
    bsonWriter.writeString(status.toString());
  }

  @Override
  public Class<TicketStatus> getEncoderClass() {
    return TicketStatus.class;
  }
}
