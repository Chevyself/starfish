package com.starfishst.bot.configuration;

import com.starfishst.api.configuration.MongoConfiguration;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link com.starfishst.api.configuration.MongoConfiguration} */
public class StarfishMongoConfiguration implements MongoConfiguration {

  /** The uri to connect with mongo */
  @NotNull private final String uri;

  /** The database to use in mongo */
  @NotNull private final String database;

  /**
   * Create the mongo configuration
   *
   * @param uri the uri to connect with mongo
   * @param database the database to use
   */
  public StarfishMongoConfiguration(@NotNull String uri, @NotNull String database) {
    this.uri = uri;
    this.database = database;
  }

  @Override
  public @NotNull String getUri() {
    return this.uri;
  }

  @Override
  public @NotNull String getDatabase() {
    return this.database;
  }
}
