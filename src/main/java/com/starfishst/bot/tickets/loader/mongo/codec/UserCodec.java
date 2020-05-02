package com.starfishst.bot.tickets.loader.mongo.codec;

import com.starfishst.bot.util.Discord;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link UserImpl} for mongo */
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
