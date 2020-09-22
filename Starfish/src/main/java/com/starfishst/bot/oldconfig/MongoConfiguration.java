package com.starfishst.bot.oldconfig;

import org.jetbrains.annotations.NotNull;

/**
 * Java representation of the 'mongo' part in 'config.json'
 *
 * @author Chevy
 * @version 1.0.0
 */
public class MongoConfiguration {

  /** The uri to connect to mongo */
  @NotNull private final String uri;
  /** The database to use in mongo */
  @NotNull private final String database;

  /**
   * Create an instance
   *
   * @param uri the uri to use in mongo
   * @param database the database to usi in mongo
   */
  public MongoConfiguration(@NotNull String uri, @NotNull String database) {
    this.uri = uri;
    this.database = database;
  }

  /**
   * Get the uri to use in mongo authentication
   *
   * @return the uri as string
   */
  @NotNull
  public String getUri() {
    return uri;
  }

  /**
   * Get the uri to use in mongo
   *
   * @return the name of the database
   */
  @NotNull
  public String getDatabase() {
    return database;
  }
}
