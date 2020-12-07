package com.starfishst.bot.tickets;

import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import lombok.NonNull;
import org.bson.Document;

/**
 * Deserializes responsive messages from mongo
 *
 * @param <T> the type of responsive message
 */
public interface StarfishMessageDeserializer<T extends StarfishResponsiveMessage> {

  /**
   * Get the message from mongo
   *
   * @param id the id of the message
   * @param data the data of the message
   * @param document the document used to get the data and id
   * @return the message
   */
  @NonNull
  T getMessage(long id, @NonNull StarfishValuesMap data, @NonNull Document document);
}
