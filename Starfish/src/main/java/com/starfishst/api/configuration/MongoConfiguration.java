package com.starfishst.api.configuration;

import org.jetbrains.annotations.NotNull;

/** The configuration for the mongo loader */
public interface MongoConfiguration {

  /**
   * The uri of the mongo client
   *
   * @return the uri
   */
  @NotNull
  String getUri();

  /**
   * The database to use
   *
   * @return the name of the database
   */
  @NotNull
  String getDatabase();
}
