package com.starfishst.api.utility;

import java.util.Optional;
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
   * @return a {@link java.util.Optional} holding the nullable jda connection
   */
  @NonNull
  Optional<JDA> getJda();

  /**
   * Get the jda connection validated as not-null
   *
   * @return the jda connection
   */
  @NonNull
  JDA getJdaValidated();
}
