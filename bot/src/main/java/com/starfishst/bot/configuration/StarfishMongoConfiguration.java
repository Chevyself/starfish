package com.starfishst.bot.configuration;

import com.starfishst.api.configuration.MongoConfiguration;
import lombok.Getter;
import lombok.NonNull;

/** An implementation for {@link com.starfishst.api.configuration.MongoConfiguration} */
public class StarfishMongoConfiguration implements MongoConfiguration {

  @NonNull @Getter private final String uri;
  @NonNull @Getter private final String database;

  /**
   * Create the mongo configuration
   *
   * @param uri the uri to connect with mongo
   * @param database the database to use
   */
  public StarfishMongoConfiguration(@NonNull String uri, @NonNull String database) {
    this.uri = uri;
    this.database = database;
  }
}
