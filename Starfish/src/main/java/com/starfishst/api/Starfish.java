package com.starfishst.api;

import com.starfishst.api.addons.AddonLoader;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.loader.LanguageHandler;
import com.starfishst.api.data.loader.TicketManager;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.utility.JdaConnection;
import com.starfishst.jda.CommandManager;
import java.util.Collection;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.Validate;
import me.googas.commons.cache.Cache;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.fallback.Fallback;
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
        instance, new IllegalStateException("A bot instance hasn't been set yet"));
  }

  public static <T extends StarfishHandler> T getHandler(@NotNull Class<T> clazz) {
    return validated().getHandler(clazz);
  }

  public static <T extends StarfishHandler> @NonNull T requireHandler(@NonNull Class<T> clazz) {
    return validated().requireHandler(clazz);
  }

  public static void save() {
    validated().save();
  }

  public static void stop() {
    validated().stop();
  }

  /**
   * Set the static instance
   *
   * @param bot the instance of the bot to use as static
   * @throws IllegalStateException if attempted to set when there's already an isntance
   */
  public void set(@NonNull StarfishBot bot) {
    if (instance != null) throw new IllegalStateException("Bot has been already initialized");
    instance = bot;
  }

  public static @NonNull Logger getLogger() {
    return validated().getLogger();
  }

  public static @NonNull Configuration getConfiguration() {
    return validated().getConfiguration();
  }

  public static @NonNull Fallback getFallback() {
    return validated().getFallback();
  }

  public static @NonNull Cache getCache() {
    return validated().getCache();
  }

  public static @NonNull DataLoader getLoader() {
    return validated().getLoader();
  }

  public static @NonNull CommandManager getCommandManager() {
    return validated().getCommandManager();
  }

  public static @NonNull ListenerManager getListenerManager() {
    return validated().getListenerManager();
  }

  public static @NonNull DiscordConfiguration getDiscordConfiguration() {
    return validated().getDiscordConfiguration();
  }

  public static @NonNull Collection<StarfishHandler> getHandlers() {
    return validated().getHandlers();
  }

  public static @NonNull TicketManager getTicketManager() {
    return validated().getTicketManager();
  }

  public static AddonLoader getAddonLoader() {
    return validated().getAddonLoader();
  }

  public static @NonNull LanguageHandler getLanguageHandler() {
    return validated().getLanguageHandler();
  }

  public static @NonNull JdaConnection getJdaConnection() {
    return validated().getJdaConnection();
  }
}
