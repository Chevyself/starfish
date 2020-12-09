package com.starfishst.bot;

import com.starfishst.adapters.ColorAdapter;
import com.starfishst.adapters.QuestionAdapter;
import com.starfishst.api.Starfish;
import com.starfishst.api.StarfishBot;
import com.starfishst.api.addons.AddonLoader;
import com.starfishst.api.addons.JavaAddonLoader;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.loader.LanguageHandler;
import com.starfishst.api.data.loader.TicketManager;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.utility.JdaConnection;
import com.starfishst.bot.commands.ChannelsCommands;
import com.starfishst.bot.commands.DeveloperCommands;
import com.starfishst.bot.commands.FreelancerCommands;
import com.starfishst.bot.commands.InvoiceCommands;
import com.starfishst.bot.commands.ModerationCommands;
import com.starfishst.bot.commands.PermissionCommands;
import com.starfishst.bot.commands.PortfolioCommands;
import com.starfishst.bot.commands.SetCommands;
import com.starfishst.bot.commands.TicketCommands;
import com.starfishst.bot.configuration.StarfishConfiguration;
import com.starfishst.bot.configuration.StarfishDiscordConfiguration;
import com.starfishst.bot.handlers.freelancer.FreelancerHandler;
import com.starfishst.bot.handlers.lang.StarfishLanguageHandler;
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
import com.starfishst.bot.handlers.ticket.transcript.TicketTranscriptHandler;
import com.starfishst.bot.tickets.StarfishDataLoader;
import com.starfishst.bot.tickets.StarfishTicketLoaderFallback;
import com.starfishst.bot.tickets.StarfishTicketManager;
import com.starfishst.bot.utility.Mongo;
import com.starfishst.jda.CommandManager;
import com.starfishst.jda.commands.FallbackCommands;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.CoreFiles;
import me.googas.commons.Lots;
import me.googas.commons.ProgramArguments;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.MemoryCache;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.gson.GsonProvider;
import me.googas.commons.gson.adapters.time.TimeAdapter;
import me.googas.commons.log.LoggerFactory;
import me.googas.commons.log.formatters.CustomFormatter;
import me.googas.commons.scheduler.Scheduler;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.JDA;

public class SimpleStarfish implements StarfishBot {

  @NonNull private final JdaConnection connection;
  @NonNull private final Set<StarfishHandler> handlers;
  @NonNull @Setter private Logger log;
  @NonNull @Setter private Configuration configuration;
  @NonNull @Setter private DiscordConfiguration discordConfiguration;
  @NonNull @Setter private Fallback fallback;
  @NonNull @Setter private Cache cache;
  @NonNull @Setter private CommandManager manager;
  @NonNull @Setter private ListenerManager listenerManager;
  @NonNull @Setter private TicketManager ticketManager;
  @NonNull @Setter private AddonLoader loader;

  @NonNull @Setter private Scheduler scheduler;

  public SimpleStarfish(
      @NonNull Logger log,
      @NonNull Configuration configuration,
      @NonNull DiscordConfiguration discordConfiguration,
      @NonNull Fallback fallback,
      @NonNull Cache cache,
      @NonNull JdaConnection connection,
      @NonNull CommandManager manager,
      @NonNull ListenerManager listenerManager,
      @NonNull TicketManager ticketManager,
      @NonNull Set<StarfishHandler> handlers,
      @NonNull AddonLoader loader,
      @NonNull Scheduler scheduler) {
    this.log = log;
    this.configuration = configuration;
    this.discordConfiguration = discordConfiguration;
    this.fallback = fallback;
    this.cache = cache;
    this.connection = connection;
    this.ticketManager = ticketManager;
    this.handlers = handlers;
    this.manager = manager;
    this.listenerManager = listenerManager;
    this.loader = loader;
    this.scheduler = scheduler;
  }

  public static void main(String[] args) {
    ProgramArguments arguments = ProgramArguments.construct(args);

    CustomFormatter formatter =
        new CustomFormatter(
            "[%level%] %day%/%month%/%year% [Guido] %hour%:%minute%:%second%: %message% %stack% \n");
    Logger log = initLogger(arguments, formatter);
    Fallback fallback = new StarfishFallback(log, new ArrayList<>());
    ListenerManager listenerManager = new ListenerManager();
    Scheduler scheduler = new StarfishScheduler();
    Thread.setDefaultUncaughtExceptionHandler(
        (thread, e) -> fallback.process(e, "Uncaught exception"));

    GsonProvider.addAdapter(Question.class, new QuestionAdapter());
    GsonProvider.addAdapter(Color.class, new ColorAdapter());
    GsonProvider.addAdapter(Time.class, new TimeAdapter());
    GsonProvider.refresh();

    Configuration configuration = null;
    try {
      configuration = initConfiguration();
      log.finest("Configuration has been loaded");
    } catch (IOException e) {
      fallback.process(e, "IOException: config.json could not be loaded");
    }

    DiscordConfiguration discordConfiguration = null;
    try {
      discordConfiguration = initDiscordConfiguration();
      log.finest("Discord configuration has been loaded");
    } catch (IOException e) {
      fallback.process(e, "IOException: discord.json could not be loaded");
    }
    if (validateOrClose(configuration == null, log, "config.json could not be initialized")) return;
    if (validateOrClose(discordConfiguration == null, log, "discord.json could not be initialized"))
      return;
    StarfishJdaConnection connection = new StarfishJdaConnection(log, configuration);
    JDA jda = connection.createConnection(arguments.getProperty("token", ""));
    DataLoader loader = new StarfishTicketLoaderFallback();
    try {
      loader =
          new StarfishDataLoader(
              Mongo.createGson(),
              configuration.getMongoConfiguration().getUri(),
              configuration.getMongoConfiguration().getDatabase());
    } catch (Exception e) {
      // I don't know for certain which is the exception that mongo throws when
      // it fails to authenticate or times out
      fallback.process(e, "Mongo could not be setup");
    }
    LanguageHandler languageHandler = new StarfishLanguageHandler().load(fallback, "en");
    Set<StarfishHandler> handlers =
        Lots.set(
            loader,
            languageHandler,
            new FreelancerHandler(),
            new AutoSaveHandler(),
            new CleanerHandler(),
            new DebugHandler(),
            new UpdateTicketName(),
            new QuestionsHandler(loader),
            new TicketTranscriptHandler(),
            new QuoteHandler(),
            new TicketAnnouncementHandler(),
            new TicketCreatorHandler(),
            new TicketHandler());
    for (StarfishHandler handler : handlers) {
      handler.register(jda, listenerManager);
    }
    StarfishProvidersRegistry registry = new StarfishProvidersRegistry(languageHandler);
    CommandManager commandManager =
        new CommandManager(
            jda,
            configuration.getPrefix(),
            configuration.getManagerOptions(),
            languageHandler,
            registry,
            new StarfishPermissionChecker(languageHandler, loader));
    for (Object command :
        Lots.list(
            new FallbackCommands(fallback),
            new ChannelsCommands(),
            new DeveloperCommands(),
            new FreelancerCommands(),
            new InvoiceCommands(),
            new ModerationCommands(),
                new PermissionCommands(),
            new PortfolioCommands(),
            new SetCommands(),
            new TicketCommands())) {
      commandManager.registerCommand(command);
    }
    AddonLoader addonLoader = null;
    try {
      addonLoader =
          new JavaAddonLoader(
              CoreFiles.directoryOrCreate(CoreFiles.currentDirectory() + "/addons"));
    } catch (IOException e) {
      fallback.process(e, "Addon loader could not be setup");
    }
    if (validateOrClose(addonLoader == null, log, "Addon Loader not be initialized")) return;
    SimpleStarfish starfish =
        new SimpleStarfish(
            log,
            configuration,
            discordConfiguration,
            fallback,
            new MemoryCache(),
            connection,
            commandManager,
            listenerManager,
            new StarfishTicketManager(loader, configuration),
            handlers,
            addonLoader,
            scheduler);
    Starfish.setInstance(starfish);
    starfish.getHandlers().forEach(StarfishHandler::onEnable);
    log.info("Bot is ready to use");
  }

  private static DiscordConfiguration initDiscordConfiguration() throws IOException {
    FileReader reader =
        new FileReader(CoreFiles.getFileOrResource(CoreFiles.currentDirectory(), "discord.json"));
    DiscordConfiguration configuration =
        GsonProvider.GSON.fromJson(reader, StarfishDiscordConfiguration.class);
    reader.close();
    return configuration;
  }

  @NonNull
  private static Logger initLogger(ProgramArguments arguments, CustomFormatter formatter) {
    Logger log;
    try {
      log = LoggerFactory.start("Starfish", formatter, "log-" + System.currentTimeMillis());
    } catch (IOException e) {
      log = LoggerFactory.start("Starfish", LoggerFactory.getConsoleHandler(formatter));
    }
    if (arguments.containsFlag("-debug", false)) {
      log.setLevel(Level.FINEST);
      log.info("Logger has been initialized in debug mode");
    } else {
      log.info("Logger has been initialized");
    }
    return log;
  }

  @NonNull
  private static Configuration initConfiguration() throws IOException {
    FileReader reader =
        new FileReader(CoreFiles.getFileOrResource(CoreFiles.currentDirectory(), "config.json"));
    Configuration configuration = GsonProvider.GSON.fromJson(reader, StarfishConfiguration.class);
    reader.close();
    return configuration;
  }

  /**
   * Validates whether a boolean should close the bot
   *
   * @param validation the boolean that must be true to close the bot
   * @param log the logger to print the given message
   * @param message the message for the client to know what made it close
   * @return true if the bot was closed
   */
  private static boolean validateOrClose(
      boolean validation, @NonNull Logger log, @NonNull String message) {
    if (validation) {
      log.severe(message);
      System.exit(0);
      return true;
    }
    return false;
  }

  @Override
  public @NonNull Logger getLogger() {
    return log;
  }

  @Override
  public @NonNull Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public @NonNull DiscordConfiguration getDiscordConfiguration() {
    return discordConfiguration;
  }

  @Override
  public @NonNull Fallback getFallback() {
    return fallback;
  }

  @Override
  public @NonNull Cache getCache() {
    return cache;
  }

  @Override
  public @NonNull CommandManager getCommandManager() {
    return manager;
  }

  @Override
  public @NonNull ListenerManager getListenerManager() {
    return listenerManager;
  }

  @Override
  public @NonNull Collection<StarfishHandler> getHandlers() {
    return handlers;
  }

  @Override
  public @NonNull TicketManager getTicketManager() {
    return ticketManager;
  }

  @Override
  public @NonNull AddonLoader getAddonLoader() {
    return loader;
  }

  @Override
  public @NonNull Scheduler getScheduler() {
    return scheduler;
  }

  @Override
  public @NonNull JdaConnection getJdaConnection() {
    return connection;
  }
}
