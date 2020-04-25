package com.starfishst.ethot.tickets.loader.mongo.codec;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.config.objects.freelancers.Offer;
import com.starfishst.ethot.config.objects.responsive.type.quotes.OfferAcceptResponsiveMessage;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class OfferCodec implements Codec<Offer> {

  @Override
  public Offer decode(BsonReader reader, DecoderContext decoderContext) {
    Freelancer freelancer = null;
    String offer = null;
    OfferAcceptResponsiveMessage message = null;
    reader.readStartDocument();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      if (fieldName.equalsIgnoreCase("freelancer")) {
        freelancer = Main.getManager().getLoader().getFreelancer(reader.readInt64());
      } else if (fieldName.equalsIgnoreCase("offer")) {
        offer = reader.readString();
      } else if (fieldName.equalsIgnoreCase("message")) {
        message = new OfferAcceptResponsiveMessage(reader.readInt64());
      }
    }
    reader.readEndDocument();
    if (freelancer == null || offer == null || message == null) {
      return null;
    } else {
      return new Offer(freelancer, offer, message);
    }
  }

  @Override
  public void encode(BsonWriter bsonWriter, Offer offer, EncoderContext encoderContext) {
    bsonWriter.writeStartDocument();
    bsonWriter.writeInt64("freelancer", offer.getFreelancer().getId());
    bsonWriter.writeString("offer", offer.getOffer());
    bsonWriter.writeInt64("message", offer.getMessage().getId());
    bsonWriter.writeEndDocument();
  }

  @Override
  public Class<Offer> getEncoderClass() {
    return Offer.class;
  }
}
