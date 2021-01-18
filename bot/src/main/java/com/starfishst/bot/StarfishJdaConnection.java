package com.starfishst.bot;

import com.starfishst.api.Starfish;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.utility.JdaConnection;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

/** Setups connection with JDA */
public class StarfishJdaConnection implements JdaConnection {

  private final Logger log;

  private final Configuration configuration;

  /** The jda instance used by the bot. */
  private JDA jda;

  /** Whether getting the token from config failed */
  boolean config = false;

  /**
   * The logger used to print important messages
   *
   * @param log the logger to use
   * @param configuration the configuration used to get the token in case that the bot cannot get it
   *     from arguments
   */
  public StarfishJdaConnection(Logger log, Configuration configuration) {
    this.log = log;
    this.configuration = configuration;
  }

  /**
   * Get a token from the input of the console
   *
   * @param scanner to get the input from
   * @return the token if an input was made
   */
  @NonNull
  public String getTokenFromInput(@NonNull Scanner scanner) {
    this.log.info("Insert the bot token");
    while (true) {
      if (scanner.hasNext()) {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("exit")) {
          System.exit(0);
        } else {
          return input;
        }
        break;
      }
    }
    return "";
  }

  /**
   * Try to connect to jda. This method will keep trying until it gets tired
   *
   * @param token the initial token
   * @return the jda api connection
   */
  @NonNull
  public JDA createConnection(@NonNull String token) {
    this.jda = null;
    while (this.jda == null) {
      try {
        this.jda = this.connect(token);
      } catch (LoginException e) {
        this.log.info("Discord authentication failed");
        this.log.info("Getting token from config");
        token = this.getToken();
      }
    }
    return this.jda;
  }

  /**
   * Attempts to get the token from either config or input
   *
   * @return the token
   */
  private String getToken() {
    if (this.config) {
      this.log.info("Getting from configuration failed trying to get from input");
      return this.getTokenFromInput(new Scanner(System.in));
    } else {
      this.config = true;
      return this.configuration.getToken();
    }
  }

  @Override
  public JDA connect(@NonNull String token) throws LoginException {
    JDA jda = JDABuilder.create(token, Lots.list(GatewayIntent.values())).build();
    long millis = 0;
    while (jda.getStatus() != JDA.Status.CONNECTED) {
      try {
        Thread.sleep(1);
        millis++;
      } catch (InterruptedException e) {
        Starfish.getFallback().process(e, "InterruptedException: Discord connection failed");
      }
    }
    this.log.info("Discord took " + Time.fromMillis(millis).toEffectiveString() + " to connect");
    jda.setEventManager(new AnnotatedEventManager());
    return jda;
  }

  @Override
  public JDA getJda() {
    return this.jda;
  }
}
