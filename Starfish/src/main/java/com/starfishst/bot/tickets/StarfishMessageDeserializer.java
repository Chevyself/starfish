package com.starfishst.bot.tickets;

import com.starfishst.bot.data.StarfishPreferences;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

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
  @NotNull
  T getMessage(long id, @NotNull StarfishPreferences data, @NotNull Document document);
}
