package com.starfishst.bot;

import com.starfishst.adapters.QuestionAdapter;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.api.data.loader.TicketManager;
import com.starfishst.bot.commands.DeveloperCommands;
import com.starfishst.bot.commands.FreelancerCommands;
import com.starfishst.bot.commands.PermissionCommands;
import com.starfishst.bot.commands.SetCommands;
import com.starfishst.bot.commands.TicketCommands;
import com.starfishst.bot.commands.providers.BotUserProvider;
import com.starfishst.bot.commands.providers.BotUserSenderProvider;
import com.starfishst.bot.commands.providers.PermissibleProvider;
import com.starfishst.bot.commands.providers.TicketProvider;
import com.starfishst.bot.configuration.StarfishConfiguration;
import com.starfishst.bot.configuration.StarfishDiscordConfiguration;
import com.starfishst.bot.handlers.StarfishHandler;
import com.starfishst.bot.handlers.freelancer.FreelancerHandler;
import com.starfishst.bot.handlers.lang.StarfishLanguageHandler;
import com.starfishst.bot.handlers.lang.StarfishLocaleFile;
import com.starfishst.bot.handlers.misc.AutoSaveHandler;
import com.starfishst.bot.handlers.misc.CleanerHandler;
import com.starfishst.bot.handlers.misc.DebugHandler;
import com.starfishst.bot.handlers.misc.UpdateTicketName;
import com.starfishst.bot.handlers.questions.Question;
import com.starfishst.bot.handlers.questions.QuestionsHandler;
import com.starfishst.bot.handlers.ticket.QuoteHandler;
import com.starfishst.bot.handlers.ticket.TicketAnnouncementHandler;
import com.starfishst.bot.handlers.ticket.TicketCreatorHandler;
import com.starfishst.bot.handlers.ticket.TicketHandler;
import com.starfishst.bot.tickets.StarfishLoader;
import com.starfishst.bot.tickets.StarfishTicketLoader;
import com.starfishst.bot.tickets.StarfishTicketLoaderFallback;
import com.starfishst.bot.tickets.StarfishTicketManager;
import com.starfishst.commands.CommandManager;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.providers.registry.ProvidersRegistryJDA;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.providers.registry.ProvidersRegistry;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.cache.ICatchable;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.core.utils.time.Time;
import com.starfishst.utils.events.ListenerManager;
import com.starfishst.utils.gson.GsonProvider;
import com.starfishst.utils.gson.adapters.core.ColorAdapter;
import com.starfishst.utils.gson.adapters.core.TimeAdapter;
import com.starfishst.utils.gson.adapters.jda.CategoryAdapter;
import com.starfishst.utils.gson.adapters.jda.GuildAdapter;
import com.starfishst.utils.gson.adapters.jda.RoleAdapter;
import com.starfishst.utils.gson.adapters.jda.TextChannelAdapter;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.internal.entities.CategoryImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.RoleImpl;
import net.dv8tion.jda.internal.entities.TextChannelImpl;
import org.jetbrains.annotations.NotNull;

/** The main class of the starfish bot */
public class Starfish {

  /** The manager for listeners and events */
  @NotNull private static final ListenerManager listenerManager = new ListenerManager();
  /** The list of handlers registered in the bot */
  @NotNull private static final List<StarfishHandler> handlers = new ArrayList<>();
  /** The jda connection for starfish */
  @NotNull private static StarfishJdaConnection connection = new StarfishJdaConnection();
  /** The configuration that the bot will be using */
  @NotNull private static Configuration configuration = StarfishConfiguration.fallback();
  /** The discord configuration of the bot */
  @NotNull
  private static DiscordConfiguration discordConfiguration =
      StarfishDiscordConfiguration.fallback();
  /** The data loader that the bot will be using */
  @NotNull private static StarfishLoader loader = new StarfishTicketLoaderFallback();

  @NotNull
  private static TicketManager ticketManager = new StarfishTicketManager(loader, configuration);
  /** The language handler for users using the bot */
  @NotNull
  private static final StarfishLanguageHandler languageHandler =
      new StarfishLanguageHandler(loader);

  /**
   * The main method of the bot
   *
   * @param args the arguments include:
   */
  public static void main(String[] args) {
    HashMap<String, String> argsMaps = Maps.fromStringArray("=", args);
    connection.createConnection(argsMaps.getOrDefault("token", ""));
    JDA jda = connection.validatedJda();
    jda.setEventManager(new AnnotatedEventManager());
    GsonProvider.addAdapter(Question.class, new QuestionAdapter());
    GsonProvider.addAdapter(Color.class, new ColorAdapter());
    GsonProvider.addAdapter(Time.class, new TimeAdapter());
    GsonProvider.addAdapter(TextChannel.class, new TextChannelAdapter(jda));
    GsonProvider.addAdapter(Role.class, new RoleAdapter(jda));
    GsonProvider.addAdapter(Guild.class, new GuildAdapter(jda));
    GsonProvider.addAdapter(Category.class, new CategoryAdapter(jda));
    GsonProvider.addAdapter(TextChannelImpl.class, new TextChannelAdapter(jda));
    GsonProvider.addAdapter(RoleImpl.class, new RoleAdapter(jda));
    GsonProvider.addAdapter(GuildImpl.class, new GuildAdapter(jda));
    GsonProvider.addAdapter(CategoryImpl.class, new CategoryAdapter(jda));
    GsonProvider.refresh();
    try {
      FileReader reader =
          new FileReader(CoreFiles.getFileOrResource(CoreFiles.currentDirectory(), "config.json"));
      configuration = GsonProvider.GSON.fromJson(reader, StarfishConfiguration.class);
    } catch (IOException e) {
      Fallback.addError("IOException: config.json could not be loaded");
      e.printStackTrace();
    }
    try {
      FileReader reader =
          new FileReader(CoreFiles.getFileOrResource(CoreFiles.currentDirectory(), "discord.json"));
      discordConfiguration = GsonProvider.GSON.fromJson(reader, StarfishDiscordConfiguration.class);
    } catch (IOException e) {
      Fallback.addError("IOException: discord.json could not be loaded");
      e.printStackTrace();
    }
    loader = new StarfishTicketLoader(jda, configuration.getMongoConfiguration());
    languageHandler.setDataLoader(loader);
    handlers.addAll(
        Lots.list(
            loader,
            languageHandler,
            new FreelancerHandler(),
            new AutoSaveHandler(),
            new CleanerHandler(),
            new DebugHandler(),
            new UpdateTicketName(),
            new QuestionsHandler(loader),
            new QuoteHandler(),
            new TicketAnnouncementHandler(),
            new TicketCreatorHandler(),
            new TicketHandler()));
    for (StarfishHandler handler : handlers) {
      handler.register(jda);
    }
    languageHandler.load("en");
    ProvidersRegistry<CommandContext> registry = new ProvidersRegistryJDA(languageHandler);
    registry.addProvider(new PermissibleProvider());
    registry.addProvider(new BotUserProvider());
    registry.addProvider(new BotUserSenderProvider());
    registry.addProvider(new TicketProvider());
    CommandManager manager =
        new CommandManager(
            jda,
            "-",
            configuration.getManagerOptions(),
            languageHandler,
            registry,
            new StarfishPermissionChecker(languageHandler, loader));
    manager.registerCommand(new DeveloperCommands());
    manager.registerCommand(new FreelancerCommands());
    manager.registerCommand(new PermissionCommands());
    manager.registerCommand(new SetCommands());
    manager.registerCommand(new TicketCommands());
    ticketManager.setDataLoader(loader);
    ticketManager.setConfiguration(configuration);
  }

  /** Saves configuration */
  public static void save() {
    try {
      File file = CoreFiles.getOrCreate(CoreFiles.currentDirectory(), "config.json");
      FileWriter writer = new FileWriter(file);
      try {
        GsonProvider.GSON.toJson(configuration, writer);
      } catch (Exception e) {
        Fallback.addError("'config.json' could not be written!");
        e.printStackTrace();
      }
      writer.close();
    } catch (IOException e) {
      Fallback.addError("IOException: config.json could not be saved");
      e.printStackTrace();
    }
    try {
      File file = CoreFiles.getOrCreate(CoreFiles.currentDirectory(), "discord.json");
      FileWriter writer = new FileWriter(file);
      try {
        GsonProvider.GSON.toJson(discordConfiguration, writer);
      } catch (Exception e) {
        Fallback.addError("'discord.json' could not be written!");
        e.printStackTrace();
      }
      writer.close();
    } catch (IOException e) {
      Fallback.addError("IOException: discord.json could not be saved");
      e.printStackTrace();
    }
    for (StarfishLocaleFile file : languageHandler.getFiles()) {
      file.save();
    }
  }

  /** Stops the bot */
  public static void stop() {
    List<ICatchable> cacheCopy = new ArrayList<>(Cache.getCache());
    cacheCopy.forEach(ICatchable::unload);
    for (StarfishHandler handler : handlers) {
      handler.onUnload();
    }
    handlers.clear();
    JDA jda = connection.getJda();
    if (jda != null) {
      jda.shutdownNow();
    }
    System.exit(0);
  }

  /**
   * Get the listener manager that the bot is using
   *
   * @return the listener manager
   */
  @NotNull
  public static ListenerManager getListenerManager() {
    return listenerManager;
  }

  /**
   * Get the configuration that the bot is using
   *
   * @return the bot configuration
   */
  @NotNull
  public static Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Get the discord configuration that the bot is using
   *
   * @return the discord configuration
   */
  @NotNull
  public static DiscordConfiguration getDiscordConfiguration() {
    return discordConfiguration;
  }

  /**
   * Get the language handler that the bot is using
   *
   * @return the language handler
   */
  @NotNull
  public static StarfishLanguageHandler getLanguageHandler() {
    return languageHandler;
  }

  /**
   * Get the bot connection with jda
   *
   * @return the bot jda connection
   */
  @NotNull
  public static StarfishJdaConnection getConnection() {
    return connection;
  }

  /**
   * Get the loader for data
   *
   * @return the loader of data
   */
  @NotNull
  public static StarfishLoader getLoader() {
    return loader;
  }

  /**
   * Get the ticket manager that the bot is using
   *
   * @return the ticket manager
   */
  @NotNull
  public static TicketManager getTicketManager() {
    return ticketManager;
  }

  /**
   * Get a handler using its class
   *
   * @param clazz the class of the handler
   * @param <T> the type of the handler
   * @return the handler
   * @throws IllegalArgumentException if the handler is not found because it means it was not
   *     registered
   */
  @NotNull
  public static <T extends StarfishHandler> T getHandler(@NotNull Class<T> clazz) {
    for (StarfishHandler handler : handlers) {
      if (handler.getClass().equals(clazz)) {
        return clazz.cast(handler);
      }
    }
    throw new IllegalArgumentException("The handler " + clazz + " may not be registered");
  }
}
