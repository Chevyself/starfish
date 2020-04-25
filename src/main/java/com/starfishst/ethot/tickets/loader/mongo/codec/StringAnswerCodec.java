package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.config.objects.questions.StringAnswer;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class StringAnswerCodec implements Codec<StringAnswer> {

  @Override
  public StringAnswer decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return new StringAnswer(bsonReader.readString());
  }

  @Override
  public void encode(
      BsonWriter bsonWriter, StringAnswer stringAnswer, EncoderContext encoderContext) {
    bsonWriter.writeString(stringAnswer.getAnswer());
  }

  @Override
  public Class<StringAnswer> getEncoderClass() {
    return StringAnswer.class;
  }
}
