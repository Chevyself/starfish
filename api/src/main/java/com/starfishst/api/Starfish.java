package com.starfishst.api;

import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.loader.LanguageHandler;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.loader.TicketManager;
import com.starfishst.api.utility.JdaConnection;
import com.starfishst.commands.jda.CommandManager;
import java.util.Collection;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.addons.AddonLoader;
import me.googas.commons.Validate;
import me.googas.commons.cache.Cache;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.scheduler.Scheduler;
import org.jetbrains.annotations.NotNull;

/** This class contains delegated static methods of {@link StarfishBot} */
public class Starfish {

  /** The static instance this will use for delegated methods */
  @Getter @Setter private static StarfishBot instance;

  /**
   * Get the instance validated as initialized
   *
   * @return the validated instance
   */
  @NonNull
  public static StarfishBot validated() {
    return Validate.notNull(
            Starfish.instance, new IllegalStateException("A bot instance hasn't been set yet"));
  }

  public static <T extends StarfishHandler> T getHandler(@NotNull Class<T> clazz) {
    return Starfish.validated().getHandler(clazz);
  }

  public static <T extends StarfishHandler> @NonNull T requireHandler(@NonNull Class<T> clazz) {
    return Starfish.validated().requireHandler(clazz);
  }

  public static void save() {
      Starfish.validated().save();
  }

  public static void stop() {
      Starfish.validated().stop();
  }

  /**
   * Set the static instance
   *
   * @param bot the instance of the bot to use as static
   * @throws IllegalStateException if attempted to set when there's already an isntance
   */
  public void set(@NonNull StarfishBot bot) {
    if (Starfish.instance != null) throw new IllegalStateException("Bot has been already initialized");
      Starfish.instance = bot;
  }

  public static @NonNull Configuration getConfiguration() {
    return Starfish.validated().getConfiguration();
  }

  public static @NonNull Fallback getFallback() {
    return Starfish.validated().getFallback();
  }

  public static @NonNull Cache getCache() {
    return Starfish.validated().getCache();
  }

  public static @NonNull Loader getLoader() {
    return Starfish.validated().getLoader();
  }

  public static @NonNull CommandManager getCommandManager() {
    return Starfish.validated().getCommandManager();
  }

  public static @NonNull ListenerManager getListenerManager() {
    return Starfish.validated().getListenerManager();
  }

  public static @NonNull DiscordConfiguration getDiscordConfiguration() {
    return Starfish.validated().getDiscordConfiguration();
  }

  public static @NonNull Collection<StarfishHandler> getHandlers() {
    return Starfish.validated().getHandlers();
  }

  public static @NonNull TicketManager getTicketManager() {
    return Starfish.validated().getTicketManager();
  }

  public static AddonLoader getAddonLoader() {
    return Starfish.validated().getAddonLoader();
  }

  public static @NonNull LanguageHandler getLanguageHandler() {
    return Starfish.validated().getLanguageHandler();
  }

  public static @NonNull JdaConnection getJdaConnection() {
    return Starfish.validated().getJdaConnection();
  }

  public static @NonNull Scheduler getScheduler() {
    return Starfish.validated().getScheduler();
  }
}
