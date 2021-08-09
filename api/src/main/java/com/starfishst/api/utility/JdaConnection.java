package com.starfishst.api.utility;

import javax.security.auth.login.LoginException;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;

/** Setups connection with JDA */
public interface JdaConnection {

  /**
   * Connects to discord
   *
   * @param token the discord bot token
   * @return the jda api
   * @throws LoginException if the discord token is wrong and authentication failed
   */
  JDA connect(@NonNull String token) throws LoginException;

  /**
   * Get the jda connection
   *
   * @return the jda connection
   */
  JDA getJda();

  /**
   * Get the jda connection validated as not-null
   *
   * @return the jda connection
   */
  @NonNull
  JDA getJdaValidated();
}
