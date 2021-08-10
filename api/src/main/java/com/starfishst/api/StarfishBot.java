package com.starfishst.api;

import com.google.gson.Gson;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.loader.LanguageHandler;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.loader.TicketManager;
import com.starfishst.api.utility.JdaConnection;
import com.starfishst.api.utility.StarfishCatchable;
import lombok.NonNull;
import me.googas.commands.jda.CommandManager;
import me.googas.io.context.Json;
import me.googas.net.cache.Cache;
import me.googas.net.cache.Catchable;
import me.googas.starbox.addons.AddonLoader;
import me.googas.starbox.events.ListenerManager;
import me.googas.starbox.scheduler.Scheduler;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/** This object represents an Starfish bot instance */
public interface StarfishBot {

  /**
   * Get the handler that matches the given class
   *
   * @param clazz the class of the handler to get
   * @param <T> the type of the handler to get
   * @return a {@link java.util.Optional} holding the nullable handler
   */
  @NonNull
  default <T extends StarfishHandler> Optional<T> getHandler(@NotNull Class<T> clazz) {
    return this.getHandlers().stream().filter(handler -> clazz.isAssignableFrom(handler.getClass())).map(clazz::cast).findFirst();
  }

  /**
   * @see #getHandler(Class) this validates that the handler is not null
   * @param clazz the class of the handler to get
   * @param <T> the type of the handler to get
   * @return the handler if found else
   * @throws NullPointerException as the handler has not been registered
   */
  @NonNull
  default <T extends StarfishHandler> T requireHandler(@NonNull Class<T> clazz) {
    return this.getHandler(clazz).orElseThrow(() -> new NullPointerException("The handler " + clazz + " has not been registered"));
  }

  /** Saves the bot */
  default void save() {
    Fallback fallback = this.getFallback();
    StarfishFiles.CONFIG.write(this.getJson(), this.getConfiguration()).handle(e -> {
      fallback.process(e, "There's been an error while trying to save 'config.json'");
    }).provide();
    StarfishFiles.DISCORD.write(this.getJson(), this.getDiscordConfiguration()).handle(e -> {
      fallback.process(e, "There's been an error while trying to save 'discord.json'");
    });
    for (LocaleFile file : this.getLanguageHandler().getFiles()) {
      file.save();
    }
  }

  default void saveCache() {
    this.getCache().keySetCopy().forEach(reference -> {
      Catchable catchable = reference.get();
      if (catchable instanceof StarfishCatchable) {
        ((StarfishCatchable) catchable).unload();
      }
    });
  }

  /** Stops the bot */
  default void stop() {
    this.saveCache();
    Collection<StarfishHandler> handlers = this.getHandlers();
    this.getJdaConnection().getJda().ifPresent(jda -> {
      handlers.forEach(handler -> handler.unregister(jda));
      jda.shutdown();
    });
    handlers.clear();
    System.exit(0);
  }

  /**
   * Get the configuration of the bot
   *
   * @return the configuration
   */
  @NonNull
  Configuration getConfiguration();

  /**
   * Get the discord configuration of the bot
   *
   * @return the discord configuration
   */
  @NonNull
  DiscordConfiguration getDiscordConfiguration();

  /**
   * Get the fallback that the bot is using
   *
   * @return the fallback
   */
  @NonNull
  Fallback getFallback();

  /**
   * Get the cache that the bot is using
   *
   * @return the cache
   */
  @NonNull
  Cache getCache();

  /**
   * Get the data loader that the bot is using
   *
   * @return the data loader
   */
  @NonNull
  default Loader getLoader() {
    return this.requireHandler(Loader.class);
  }

  /**
   * Get the command manager of the bot
   *
   * @return the command manager
   */
  @NonNull
  CommandManager getCommandManager();

  /**
   * Get the manager of listeners that handle starfish events
   *
   * @return the listener manager
   */
  @NonNull
  ListenerManager getListenerManager();

  /**
   * Get the collection of handlers that have been registered in the bot
   *
   * @return the collection of registered handlers
   */
  @NonNull
  Collection<StarfishHandler> getHandlers();

  /**
   * Get the manager of tickets
   *
   * @return the ticket manager
   */
  @NonNull
  TicketManager getTicketManager();

  /**
   * Get the addon loader that the bot is using
   *
   * @return the addon loader
   */
  @NonNull
  AddonLoader getAddonLoader();

  /**
   * Get the scheduler used to run tasks in starfish
   *
   * @return the scheduler
   */
  @NonNull
  Scheduler getScheduler();

  /**
   * The gson instance that the bot is using to deserialize stuff
   *
   * @return the gson instance
   */
  @NonNull @Deprecated
  Gson getGson();

  @NonNull
  Json getJson();

  /**
   * Get the language handler that the bot is using
   *
   * @return the language handler
   */
  @NonNull
  default LanguageHandler getLanguageHandler() {
    return this.requireHandler(LanguageHandler.class);
  }

  @NonNull
  JdaConnection getJdaConnection();
}
