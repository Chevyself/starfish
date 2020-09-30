package com.starfishst.bot;

import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.Validate;
import com.starfishst.core.utils.time.Time;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Setups connection with JDA */
public class StarfishJdaConnection {

  /** The jda instance used by the bot. */
  @Nullable private JDA jda;

  /**
   * Get a token from the input of the console
   *
   * @param scanner to get the input from
   * @return the token if an input was made
   */
  @NotNull
  public static String getTokenFromInput(@NotNull Scanner scanner) {
    System.out.println("Insert the bot token");
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
  @NotNull
  public JDA createConnection(@NotNull String token) {
    jda = null;
    while (jda == null) {
      try {
        jda = this.connect(token);
      } catch (LoginException e) {
        System.out.println("Discord authentication failed");
        token = getTokenFromInput(new Scanner(System.in));
      }
    }
    return jda;
  }

  /**
   * Connects to discord
   *
   * @param token the discord bot token
   * @return the jda api
   * @throws LoginException if the discord token is wrong and authentication failed
   */
  public JDA connect(@NotNull String token) throws LoginException {
    JDA jda = JDABuilder.create(token, Lots.list(GatewayIntent.values())).build();
    long millis = 0;
    while (jda.getStatus() != JDA.Status.CONNECTED) {
      try {
        Thread.sleep(1);
        millis++;
      } catch (InterruptedException e) {
        Fallback.addError("InterruptedException: Discord connection failed");
        e.printStackTrace();
      }
    }
    System.out.println(
        "Discord took " + Time.fromMillis(millis).toEffectiveString() + " to connect");
    return jda;
  }

  /**
   * Validate the connection with jda to not be null
   *
   * @return the validated connection
   */
  @NotNull
  public JDA validatedJda() {
    return Validate.notNull(this.jda, "Bot is not connected with discord!");
  }

    /**
   * Get the connection with jda
   *
   * @return the connection with jda
   */
  @Nullable
  public JDA getJda() {
    return jda;
  }
}
