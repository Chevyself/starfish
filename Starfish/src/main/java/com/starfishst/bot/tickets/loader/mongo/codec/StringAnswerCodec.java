package com.starfishst.bot.tickets.loader.mongo.codec;

import com.starfishst.bot.objects.questions.ImageAnswer;
import com.starfishst.bot.objects.questions.StringAnswer;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link StringAnswer} for mongo */
public class StringAnswerCodec implements Codec<StringAnswer> {

  @Override
  public StringAnswer decode(BsonReader bsonReader, DecoderContext context) {
    String string = bsonReader.readString();
    if (string.toLowerCase().startsWith("image")) {
      return new ImageAnswer(ImageAnswer.removePrefix(string));
    } else {
      return new StringAnswer(string);
    }
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