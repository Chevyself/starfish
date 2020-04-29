package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.util.Discord;
import net.dv8tion.jda.api.entities.Role;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link Role} for mongo */
public class RoleCodec implements Codec<Role> {

  @Override
  public void encode(BsonWriter bsonWriter, Role role, EncoderContext encoderContext) {
    bsonWriter.writeInt64(role.getIdLong());
  }

  @Override
  public Class<Role> getEncoderClass() {
    return Role.class;
  }

  @Override
  public Role decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return Discord.getRole(bsonReader.readInt64());
  }
}
