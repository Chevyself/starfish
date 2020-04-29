package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.objects.questions.Answer;
import com.starfishst.ethot.objects.questions.RoleAnswer;
import com.starfishst.ethot.objects.questions.StringAnswer;
import com.starfishst.ethot.tickets.loader.mongo.MongoTicketLoader;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

/** Decode and encode {@link Answer} for mongo */
public class AnswerCodec implements Codec<Answer> {

  @Override
  public Answer decode(BsonReader reader, DecoderContext context) {
    CodecRegistry codecRegistry = MongoTicketLoader.getCodecRegistry();
    if (reader.getCurrentBsonType() == BsonType.ARRAY) {
      return context.decodeWithChildContext(codecRegistry.get(RoleAnswer.class), reader);
    }
    return context.decodeWithChildContext(codecRegistry.get(StringAnswer.class), reader);
  }

  @Override
  public void encode(BsonWriter bsonWriter, Answer answer, EncoderContext encoderContext) {}

  @Override
  public Class<Answer> getEncoderClass() {
    return Answer.class;
  }
}
