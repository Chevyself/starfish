package com.starfishst.ethot.config;

import org.jetbrains.annotations.NotNull;

/**
 * Java representation of the 'mongo' part in 'config.json'
 *
 * @author Chevy
 * @version 1.0.0
 */
public class MongoConfiguration {

  @NotNull private final String uri;
  @NotNull private final String database;

  public MongoConfiguration(@NotNull String uri, @NotNull String database) {
    this.uri = uri;
    this.database = database;
  }

  @NotNull
  public String getUri() {
    return uri;
  }

  @NotNull
  public String getDatabase() {
    return database;
  }
}
