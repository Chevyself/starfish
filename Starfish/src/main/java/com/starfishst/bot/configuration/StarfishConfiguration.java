package com.starfishst.bot.configuration;

import com.google.gson.annotations.SerializedName;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.utility.Fee;
import com.starfishst.bot.handlers.StarfishHandlerValuesMap;
import com.starfishst.bot.utility.Mongo;
import com.starfishst.jda.ManagerOptions;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.CoreFiles;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

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
  private final ManagerOptions managerOptions;

  @SerializedName("handlers")
  @NonNull
  @Getter
  private final HashMap<String, StarfishHandlerValuesMap> handlerPreferences;

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
   * This constructor is used for gson. Use {@link me.googas.commons.fallback.Fallback} for a
   * configuration with no constructor
   */
  @Deprecated
  public StarfishConfiguration() {
    this(
        "Tolen",
        "en",
        0,
        new ArrayList<>(),
        new Time(25, Unit.MINUTES),
        new Time(25, Unit.MINUTES),
        new Time(25, Unit.MINUTES),
        new StarfishMongoConfiguration("", ""),
        new ManagerOptions(),
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
   * @param managerOptions the options for commands
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
      @NonNull ManagerOptions managerOptions,
      @NonNull HashMap<String, StarfishHandlerValuesMap> handlerPreferences,
      @NonNull String prefix) {
    this.token = token;
    this.lang = lang;
    this.total = total;
    this.fees = fees;
    this.unloadTickets = unloadTickets;
    this.unloadUsers = unloadUsers;
    this.unloadMessages = unloadMessages;
    this.mongoConfiguration = mongoConfiguration;
    this.managerOptions = managerOptions;
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
        new Time(25, Unit.MINUTES),
        new Time(25, Unit.MINUTES),
        new Time(25, Unit.MINUTES),
        new StarfishMongoConfiguration("", ""),
        new ManagerOptions(),
        new HashMap<>(),
        "-");
  }

  @NonNull
  public static StarfishConfiguration init() {
    FileReader reader = null;
    StarfishConfiguration configuration = fallback();
    try {
      reader =
          new FileReader(CoreFiles.getFileOrResource(CoreFiles.currentDirectory(), "config.json"));
      configuration = Mongo.GSON.fromJson(reader, StarfishConfiguration.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return configuration;
  }
}
