package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.util.Discord;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserCodec implements Codec<UserImpl> {

  @Override
  public UserImpl decode(BsonReader reader, DecoderContext context) {
    return (UserImpl) Discord.getUser(reader.readInt64());
  }

  @Override
  public void encode(BsonWriter writer, UserImpl user, EncoderContext encoderContext) {
    writer.writeInt64(user.getIdLong());
  }

  @Override
  public Class<UserImpl> getEncoderClass() {
    return UserImpl.class;
  }
}
