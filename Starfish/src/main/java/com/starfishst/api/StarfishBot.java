package com.starfishst.api;

import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.loader.LanguageHandler;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.loader.TicketManager;
import com.starfishst.api.utility.JdaConnection;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.bot.utility.Mongo;
import com.starfishst.jda.CommandManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collection;
import lombok.NonNull;
import me.googas.addons.AddonLoader;
import me.googas.commons.CoreFiles;
import me.googas.commons.Validate;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.Catchable;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.scheduler.Scheduler;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/** This object represents an Starfish bot instance */
public interface StarfishBot {

  /**
   * Get the handler that matches the given class
   *
   * @param clazz the class of the handler to get
   * @param <T> the type of the handler to get
   * @return the handler if it is inside {@link #getHandlers()} else null
   */
  default <T extends StarfishHandler> T getHandler(@NotNull Class<T> clazz) {
    for (StarfishHandler handler : this.getHandlers()) {
      if (clazz.isAssignableFrom(handler.getClass())) {
        return clazz.cast(handler);
      }
    }
    return null;
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
    T handler = this.getHandler(clazz);
    return Validate.notNull(handler, "The handler " + clazz + " has not been registered");
  }

  /** Saves the bot */
  default void save() {
    Fallback fallback = this.getFallback();
    try {
      File file = CoreFiles.getOrCreate(CoreFiles.currentDirectory(), "config.json");
      FileWriter writer = new FileWriter(file);
      try {
        Mongo.GSON.toJson(this.getConfiguration(), writer);
      } catch (Exception e) {
        fallback.process(e, "'config.json' could not be written!");
      }
      writer.close();
    } catch (IOException e) {
      fallback.process(e, "IOException: config.json could not be saved");
    }
    try {
      File file = CoreFiles.getOrCreate(CoreFiles.currentDirectory(), "discord.json");
      FileWriter writer = new FileWriter(file);
      try {
        Mongo.GSON.toJson(this.getDiscordConfiguration(), writer);
      } catch (Exception e) {
        fallback.process(e, "'discord.json' could not be written!");
      }
      writer.close();
    } catch (IOException e) {
      fallback.process(e, "IOException: discord.json could not be saved");
    }
    for (LocaleFile file : this.getLanguageHandler().getFiles()) {
      file.save();
    }
  }

  /** Stops the bot */
  default void stop() {
    Collection<SoftReference<Catchable>> cacheCopy = this.getCache().copy();
    cacheCopy.forEach(
        reference -> {
          Catchable catchable = reference.get();
          if (catchable instanceof StarfishCatchable) {
            try {
              ((StarfishCatchable) catchable).unload();
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }
          }
        });
    Collection<StarfishHandler> handlers = this.getHandlers();
    JDA jda = this.getJdaConnection().getJda();
    if (jda != null) {
      for (StarfishHandler handler : handlers) {
        handler.unregister(jda);
      }
      jda.shutdownNow();
    }
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
