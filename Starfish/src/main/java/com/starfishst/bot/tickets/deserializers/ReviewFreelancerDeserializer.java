package com.starfishst.bot.tickets.deserializers;

import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.data.messages.rating.ReviewFreelancer;
import com.starfishst.bot.tickets.StarfishMessageDeserializer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/** Deserializes the freelancer review */
public class ReviewFreelancerDeserializer implements StarfishMessageDeserializer<ReviewFreelancer> {

  @NotNull
  @Override
  public ReviewFreelancer getMessage(
      long id, @NotNull StarfishValuesMap data, @NotNull Document document) {
    return new ReviewFreelancer(id, data);
  }
}
