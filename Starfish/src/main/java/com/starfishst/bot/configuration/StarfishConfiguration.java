package com.starfishst.bot.configuration;

import com.google.gson.annotations.SerializedName;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.MongoConfiguration;
import com.starfishst.api.utility.Fee;
import com.starfishst.bot.handlers.StarfishHandlerValuesMap;
import com.starfishst.jda.ManagerOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** An implementation for {@link com.starfishst.api.configuration.Configuration} */
public class StarfishConfiguration implements Configuration {

  /** The token to use to connect with discord */
  @NonNull private final String token;

  /** The default lang */
  @NonNull private final String lang;

  /** The time to unload tickets */
  @SerializedName("ticket-unload")
  @NonNull
  private final Time ticketUnload;
  /** The time to unload users */
  @SerializedName("users-unload")
  @NonNull
  private final Time usersUnload;
  /** The time to unload users */
  @SerializedName("messages-unload")
  @NonNull
  private final Time messagesUnload;

  /** The configuration for mongo */
  @NonNull private final StarfishMongoConfiguration mongo;

  /** The fees that can be applied */
  @NonNull private final List<Fee> fees;

  /** The options for commands */
  @SerializedName("commands")
  @NonNull
  private final ManagerOptions options;

  /** The preferences for handlers */
  @SerializedName("handlers")
  @NonNull
  private final HashMap<String, StarfishHandlerValuesMap> handlersPreferences;

  /** The total of tickets created */
  private long total;

  /** The prefix used in commands */
  private final String prefix;

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
   * @param ticketUnload the time to unload a ticket
   * @param usersUnload the time to unload users
   * @param messagesUnload the time to unload messages
   * @param mongo the configuration for mongo
   * @param options the options for commands
   * @param handlersPreferences the preferences for handlers
   * @param prefix
   */
  public StarfishConfiguration(
      @NonNull String token,
      @NonNull String lang,
      long total,
      @NonNull List<Fee> fees,
      @NonNull Time ticketUnload,
      @NonNull Time usersUnload,
      @NonNull Time messagesUnload,
      @NonNull StarfishMongoConfiguration mongo,
      @NonNull ManagerOptions options,
      @NonNull HashMap<String, StarfishHandlerValuesMap> handlersPreferences,
      @NonNull String prefix) {
    this.token = token;
    this.lang = lang;
    this.total = total;
    this.fees = fees;
    this.ticketUnload = ticketUnload;
    this.usersUnload = usersUnload;
    this.messagesUnload = messagesUnload;
    this.mongo = mongo;
    this.options = options;
    this.handlersPreferences = handlersPreferences;
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

  @Override
  public void setTotal(long total) {
    this.total = total;
  }

  @Override
  public long getTotal() {
    return this.total;
  }

  @Override
  public @NonNull Collection<Fee> getFees() {
    return this.fees;
  }

  /**
   * Get the token to connect with discord
   *
   * @return the token
   */
  @Override
  public @NonNull String getToken() {
    return this.token;
  }

  @Override
  public @NonNull String getPrefix() {
    return this.prefix;
  }

  /**
   * Get the default lang to which the bot will be running on
   *
   * @return the default lang
   */
  @Override
  public @NonNull String getDefaultLang() {
    return this.lang;
  }

  @Override
  public @NonNull Time toUnloadTickets() {
    return this.ticketUnload;
  }

  @Override
  public @NonNull Time toUnloadUser() {
    return this.usersUnload;
  }

  @Override
  public @NonNull Time toUnloadMessages() {
    return this.messagesUnload;
  }

  @Override
  public @NonNull MongoConfiguration getMongoConfiguration() {
    return this.mongo;
  }

  @Override
  public @NonNull ManagerOptions getManagerOptions() {
    return this.options;
  }

  @Override
  public @NonNull HashMap<String, StarfishHandlerValuesMap> getHandlerPreferences() {
    return this.handlersPreferences;
  }
}
