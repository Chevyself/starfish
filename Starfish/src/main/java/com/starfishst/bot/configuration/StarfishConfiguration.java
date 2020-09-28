package com.starfishst.bot.configuration;

import com.google.gson.annotations.SerializedName;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.MongoConfiguration;
import com.starfishst.bot.handlers.StarfishHandlerValuesMap;
import com.starfishst.commands.ManagerOptions;
import com.starfishst.core.utils.time.Time;
import com.starfishst.core.utils.time.Unit;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link com.starfishst.api.configuration.Configuration} */
public class StarfishConfiguration implements Configuration {

  /** The time to unload tickets */
  @SerializedName("ticket-unload")
  @NotNull
  private final Time ticketUnload;
  /** The time to unload users */
  @SerializedName("users-unload")
  @NotNull
  private final Time usersUnload;
  /** The time to unload users */
  @SerializedName("messages-unload")
  @NotNull
  private final Time messagesUnload;

  @NotNull private final StarfishMongoConfiguration mongo;

  @SerializedName("commands")
  @NotNull
  private final ManagerOptions options;

  @SerializedName("handlers")
  @NotNull
  private final HashMap<String, StarfishHandlerValuesMap> handlersPreferences;

  private long total;

  @SerializedName("quotes-limit")
  private long quotesLimit;

  @SerializedName("open-limit")
  private long openLimit;

  /**
   * This constructor is used for gson. Use {@link com.starfishst.core.fallback.Fallback} for a
   * configuration with no constructor
   */
  @Deprecated
  public StarfishConfiguration() {
    this(
        0,
        2,
        2,
        new Time(25, Unit.MINUTES),
        new Time(25, Unit.MINUTES),
        new Time(25, Unit.MINUTES),
        new StarfishMongoConfiguration("", ""),
        new ManagerOptions(),
        new HashMap<>());
  }

  /**
   * Create the starfish configuration
   *
   * @param total the total of tickets created
   * @param quotesLimit the limit of quotes per freelancer in a ticket
   * @param openLimit the limit of open tickets per user
   * @param ticketUnload the time to unload a ticket
   * @param usersUnload the time to unload users
   * @param messagesUnload the time to unload messages
   * @param mongo the configuration for mongo
   * @param options the options for commands
   * @param handlersPreferences the preferences for handlers
   */
  public StarfishConfiguration(
      long total,
      long quotesLimit,
      long openLimit,
      @NotNull Time ticketUnload,
      @NotNull Time usersUnload,
      @NotNull Time messagesUnload,
      @NotNull StarfishMongoConfiguration mongo,
      @NotNull ManagerOptions options,
      @NotNull HashMap<String, StarfishHandlerValuesMap> handlersPreferences) {
    this.total = total;
    this.quotesLimit = quotesLimit;
    this.openLimit = openLimit;
    this.ticketUnload = ticketUnload;
    this.usersUnload = usersUnload;
    this.messagesUnload = messagesUnload;
    this.mongo = mongo;
    this.options = options;
    this.handlersPreferences = handlersPreferences;
  }

  /**
   * Create the fallback starfish configuration
   *
   * @return the fallback starfish configuration
   */
  @NotNull
  public static StarfishConfiguration fallback() {
    return new StarfishConfiguration(
        0,
        2,
        2,
        new Time(25, Unit.MINUTES),
        new Time(25, Unit.MINUTES),
        new Time(25, Unit.MINUTES),
        new StarfishMongoConfiguration("", ""),
        new ManagerOptions(),
        new HashMap<>());
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
  public long getLimitOfQuotes() {
    return this.quotesLimit;
  }

  @Override
  public long getOpenLimit() {
    return this.openLimit;
  }

  @Override
  public @NotNull Time toUnloadTickets() {
    return this.ticketUnload;
  }

  @Override
  public @NotNull Time toUnloadUser() {
    return this.usersUnload;
  }

  @Override
  public @NotNull Time toUnloadMessages() {
    return this.messagesUnload;
  }

  @Override
  public @NotNull MongoConfiguration getMongoConfiguration() {
    return this.mongo;
  }

  @Override
  public @NotNull ManagerOptions getManagerOptions() {
    return this.options;
  }

  @Override
  public @NotNull HashMap<String, StarfishHandlerValuesMap> getHandlerPreferences() {
    return this.handlersPreferences;
  }
}
