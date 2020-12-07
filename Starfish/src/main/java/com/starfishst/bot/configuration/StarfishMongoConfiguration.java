package com.starfishst.bot.configuration;

import com.starfishst.api.configuration.MongoConfiguration;
import lombok.NonNull;

/** An implementation for {@link com.starfishst.api.configuration.MongoConfiguration} */
public class StarfishMongoConfiguration implements MongoConfiguration {

  /** The uri to connect with mongo */
  @NonNull private final String uri;

  /** The database to use in mongo */
  @NonNull private final String database;

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

  @Override
  public @NonNull String getUri() {
    return this.uri;
  }

  @Override
  public @NonNull String getDatabase() {
    return this.database;
  }
}
