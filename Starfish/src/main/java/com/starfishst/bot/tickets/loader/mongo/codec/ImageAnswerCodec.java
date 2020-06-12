package com.starfishst.bot.tickets.loader.mongo.codec;

import com.starfishst.bot.objects.questions.ImageAnswer;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link ImageAnswer} for mongo */
public class ImageAnswerCodec implements Codec<ImageAnswer> {

  @Override
  public void encode(
      BsonWriter bsonWriter, ImageAnswer imageAnswer, EncoderContext encoderContext) {
    bsonWriter.writeString(imageAnswer.getAnswer());
  }

  @Override
  public Class<ImageAnswer> getEncoderClass() {
    return ImageAnswer.class;
  }

  @Override
  public ImageAnswer decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return new ImageAnswer(ImageAnswer.removePrefix(bsonReader.readString()));
  }
}
