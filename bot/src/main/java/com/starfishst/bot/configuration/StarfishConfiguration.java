package com.starfishst.bot.configuration;

import com.google.gson.annotations.SerializedName;
import com.starfishst.api.Starfish;
import com.starfishst.api.StarfishFiles;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.utility.Fee;
import com.starfishst.api.utility.ValuesMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commands.jda.DefaultListenerOptions;
import me.googas.commands.jda.ListenerOptions;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;

/** An implementation for {@link com.starfishst.api.configuration.Configuration} */
public class StarfishConfiguration implements Configuration {

  @NonNull @Getter private final String token;

  @NonNull @Getter private final String lang;

  @SerializedName("mongo")
  @NonNull
  @Getter
  private final StarfishMongoConfiguration mongoConfiguration;

  @NonNull @Getter private final List<Fee> fees;
  @Getter private final String prefix;

  @SerializedName("commands")
  @NonNull
  @Getter
  private final ListenerOptions listenerOptions;

  @SerializedName("handlers")
  @NonNull
  @Getter
  private final HashMap<String, ValuesMap> handlerPreferences;

  @SerializedName("ticket-unload")
  @NonNull
  @Setter
  @Getter
  private Time unloadTickets;

  @SerializedName("users-unload")
  @NonNull
  @Setter
  @Getter
  private Time unloadUsers;

  @SerializedName("messages-unload")
  @NonNull
  @Setter
  @Getter
  private Time unloadMessages;

  @Setter @Getter private long total;

  /**
   * This constructor is used for gson. Use {@link com.starfishst.api.Fallback} for a configuration
   * with no constructor
   */
  @Deprecated
  public StarfishConfiguration() {
    this(
        "Tolen",
        "en",
        0,
        new ArrayList<>(),
        Time.of(25, Unit.MINUTES),
        Time.of(25, Unit.MINUTES),
        Time.of(25, Unit.MINUTES),
        new StarfishMongoConfiguration("", ""),
        new DefaultListenerOptions(),
        new HashMap<>(),
        "-");
  }

  /**
   * Create the starfish configuration
   *
   * @param token the token to connect with discord
   * @param lang the default lang that the bot runs at
   * @param total the total of tickets created
   * @param fees the fees that can be applied
   * @param unloadTickets the time to unload a ticket
   * @param unloadUsers the time to unload users
   * @param unloadMessages the time to unload messages
   * @param mongoConfiguration the configuration for mongo
   * @param listenerOptions the options for commands
   * @param handlerPreferences the preferences for handlers
   * @param prefix
   */
  public StarfishConfiguration(
      @NonNull String token,
      @NonNull String lang,
      long total,
      @NonNull List<Fee> fees,
      @NonNull Time unloadTickets,
      @NonNull Time unloadUsers,
      @NonNull Time unloadMessages,
      @NonNull StarfishMongoConfiguration mongoConfiguration,
      @NonNull ListenerOptions listenerOptions,
      @NonNull HashMap<String, ValuesMap> handlerPreferences,
      @NonNull String prefix) {
    this.token = token;
    this.lang = lang;
    this.total = total;
    this.fees = fees;
    this.unloadTickets = unloadTickets;
    this.unloadUsers = unloadUsers;
    this.unloadMessages = unloadMessages;
    this.mongoConfiguration = mongoConfiguration;
    this.listenerOptions = listenerOptions;
    this.handlerPreferences = handlerPreferences;
    this.prefix = prefix;
  }

  /**
   * Create the fallback starfish configuration
   *
   * @return the fallback starfish configuration
   */
  @NonNull
  public static StarfishConfiguration fallback() {
    return new StarfishConfiguration(
        "token",
        "en",
        0,
        new ArrayList<>(),
        Time.of(25, Unit.MINUTES),
        Time.of(25, Unit.MINUTES),
        Time.of(25, Unit.MINUTES),
        new StarfishMongoConfiguration("", ""),
        new DefaultListenerOptions(),
        new HashMap<>(),
        "-");
  }

  @NonNull
  public static StarfishConfiguration init() {
    return StarfishFiles.CONFIG
        .read(Starfish.getJson(), StarfishConfiguration.class)
        .handle(
            e -> {
              Starfish.getFallback().process(e, "Could not config.json");
            })
        .provide()
        .orElseGet(StarfishConfiguration::fallback);
  }
}
