package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.objects.questions.RoleAnswer;
import com.starfishst.ethot.tickets.loader.mongo.MongoTicketLoader;
import net.dv8tion.jda.api.entities.Role;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;

/** Decode and encode {@link RoleAnswer} for mongo */
public class RoleAnswerCodec implements Codec<RoleAnswer> {

  @Override
  public RoleAnswer decode(BsonReader bsonReader, DecoderContext decoderContext) {
    bsonReader.readStartArray();
    List<Role> roles = new ArrayList<>();
    while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      roles.add(
          decoderContext.decodeWithChildContext(
              MongoTicketLoader.getCodecRegistry().get(Role.class), bsonReader));
    }
    bsonReader.readEndArray();
    return new RoleAnswer(roles);
  }

  @Override
  public void encode(BsonWriter bsonWriter, RoleAnswer roleAnswer, EncoderContext encoderContext) {
    bsonWriter.writeStartArray();
    roleAnswer.getAnswer().forEach(role -> bsonWriter.writeInt64(role.getIdLong()));
    bsonWriter.writeEndArray();
  }

  @Override
  public Class<RoleAnswer> getEncoderClass() {
    return RoleAnswer.class;
  }
}
