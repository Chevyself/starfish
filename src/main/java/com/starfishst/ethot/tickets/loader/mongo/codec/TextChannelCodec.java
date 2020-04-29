package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.util.Discord;
import net.dv8tion.jda.internal.entities.TextChannelImpl;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link TextChannelImpl} for mongo */
public class TextChannelCodec implements Codec<TextChannelImpl> {

  @Override
  public TextChannelImpl decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return (TextChannelImpl) Discord.getTextChannel(bsonReader.readInt64());
  }

  @Override
  public void encode(
      BsonWriter bsonWriter, TextChannelImpl textChannel, EncoderContext encoderContext) {
    bsonWriter.writeInt64(textChannel.getIdLong());
  }

  @Override
  public Class<TextChannelImpl> getEncoderClass() {
    return TextChannelImpl.class;
  }
}
