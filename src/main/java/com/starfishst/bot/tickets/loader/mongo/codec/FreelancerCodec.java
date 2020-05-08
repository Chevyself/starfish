package com.starfishst.bot.tickets.loader.mongo.codec;

import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.tickets.loader.mongo.MongoTicketLoader;
import com.starfishst.bot.util.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/** Decode and encode {@link Freelancer} for mongo */
public class FreelancerCodec implements Codec<Freelancer> {

  @Override
  public Freelancer decode(BsonReader reader, DecoderContext decoderContext) {
    long id = -1;
    List<String> portfolio = new ArrayList<>();
    HashMap<String, Integer> rating = new HashMap<>();
    reader.readStartDocument();
    reader.readObjectId();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      if (fieldName.equalsIgnoreCase("id")) {
        id = reader.readInt64();
      } else if (fieldName.equalsIgnoreCase("portfolio")) {
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
          portfolio.add(reader.readString());
        }
        reader.readEndArray();
      } else if (fieldName.equalsIgnoreCase("rating")) {
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
          String user = reader.readName();
          int rate = reader.readInt32();
          rating.put(user, rate);
        }
        reader.readEndDocument();
      }
    }
    reader.readEndDocument();
    if (id == -1) {
      throw new IllegalArgumentException("Illegal freelancer id");
    } else {
      return new Freelancer(portfolio, Maps.getWithKeysAsLong(rating), id);
    }
  }

  @Override
  public void encode(BsonWriter bsonWriter, Freelancer freelancer, EncoderContext encoderContext) {
    MongoTicketLoader.getCodecRegistry()
        .get(Document.class)
        .encode(bsonWriter, freelancer.getDocument(), encoderContext);
  }

  @Override
  public Class<Freelancer> getEncoderClass() {
    return Freelancer.class;
  }
}
