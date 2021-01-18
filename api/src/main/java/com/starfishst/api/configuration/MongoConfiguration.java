package com.starfishst.api.configuration;

import lombok.NonNull;

/** The configuration for the mongo loader */
public interface MongoConfiguration {

  /**
   * The uri of the mongo client
   *
   * @return the uri
   */
  @NonNull
  String getUri();

  /**
   * The database to use
   *
   * @return the name of the database
   */
  @NonNull
  String getDatabase();
}
