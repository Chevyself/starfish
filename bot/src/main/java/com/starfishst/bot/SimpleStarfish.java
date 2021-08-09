package com.starfishst.bot;

import com.google.gson.Gson;
import com.starfishst.api.Fallback;
import com.starfishst.api.Starfish;
import com.starfishst.api.StarfishBot;
import com.starfishst.api.StarfishFiles;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.loader.LanguageHandler;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.loader.TicketManager;
import com.starfishst.api.utility.JdaConnection;
import com.starfishst.bot.commands.CacheCommands;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commands.jda.CommandManager;
import me.googas.io.context.Json;
import me.googas.net.cache.Cache;
import me.googas.net.cache.MemoryCache;
import me.googas.starbox.addons.AddonLoader;
import me.googas.starbox.addons.java.JavaAddonLoader;
import me.googas.starbox.builders.LoggerBuilder;
import me.googas.starbox.events.ListenerManager;
import me.googas.starbox.scheduler.Scheduler;
import net.dv8tion.jda.api.JDA;

public class SimpleStarfish implements StarfishBot {

  @NonNull @Getter
  private static final Formatter formatter =
      new CustomFormatter(
          "[%level%] %day%/%month%/%year% [Starfish] %hour%:%minute%:%second%: %message% %stack% \n");

  @NonNull @Getter
  public static final Logger log =
      LoggerBuilder.of("Starbox")
          .format(SimpleStarfish.formatter)
          .saveAt(
              StarfishFiles.getLogFile(),
              e -> {
                System.out.println("Could not initialize FileHandler, e");
                e.printStackTrace();
              })
          .build();

  @NonNull @Getter private final JdaConnection jdaConnection;
  @NonNull @Getter private final List<StarfishHandler> handlers; // TODO move this to registry
  @NonNull @Setter @Getter private Configuration configuration;
  @NonNull @Setter @Getter private DiscordConfiguration discordConfiguration;
  @NonNull @Setter @Getter private Fallback fallback;
  @NonNull @Setter @Getter private Cache cache;
  @NonNull @Setter @Getter private CommandManager commandManager;
  @NonNull @Setter @Getter private ListenerManager listenerManager;
  @NonNull @Setter @Getter private TicketManager ticketManager;
  @NonNull @Setter @Getter private AddonLoader addonLoader;
  @NonNull @Setter @Getter private Scheduler scheduler;
  @NonNull @Setter @Getter private Json json;

  public SimpleStarfish(
      @NonNull Configuration configuration,
      @NonNull DiscordConfiguration discordConfiguration,
      @NonNull Fallback fallback,
      @NonNull Cache cache,
      @NonNull JdaConnection jdaConnection,
      @NonNull CommandManager commandManager,
      @NonNull ListenerManager listenerManager,
      @NonNull TicketManager ticketManager,
      @NonNull List<StarfishHandler> handlers,
      @NonNull AddonLoader addonLoader,
      @NonNull Scheduler scheduler,
      @NonNull Json json) {
    this.configuration = configuration;
    this.discordConfiguration = discordConfiguration;
    this.fallback = fallback;
    this.cache = cache;
    this.jdaConnection = jdaConnection;
    this.ticketManager = ticketManager;
    this.handlers = handlers;
    this.commandManager = commandManager;
    this.listenerManager = listenerManager;
    this.addonLoader = addonLoader;
    this.scheduler = scheduler;
    this.json = json;
  }

  public static void main(String[] args) throws IOException {
    String token = args.length < 1 ? "" : args[0];
    Fallback fallback = new StarfishFallback(SimpleStarfish.log);
    ListenerManager listenerManager = new ListenerManager();
    Scheduler scheduler = new StarfishScheduler();
    Thread.setDefaultUncaughtExceptionHandler(
        (thread, e) -> fallback.process(e, "Uncaught exception"));
    Configuration configuration = StarfishConfiguration.init();
    DiscordConfiguration discordConfiguration = StarfishDiscordConfiguration.init();
    StarfishJdaConnection connection = new StarfishJdaConnection(SimpleStarfish.log, configuration);
    JDA jda = connection.createConnection(token);
    Loader loader = new StarfishTicketLoaderFallback();
    try {
      loader =
          new StarfishDataLoader(
              configuration.getMongoConfiguration().getUri(),
              configuration.getMongoConfiguration().getDatabase());
    } catch (Exception e) {
      // I don't know for certain which is the exception that mongo throws when
      // it fails to authenticate or times out
      fallback.process(e, "Mongo could not be setup");
    }
    LanguageHandler languageHandler = new StarfishLanguageHandler().load(fallback, "en");
    List<StarfishHandler> handlers =
        Arrays.asList(
            loader,
            languageHandler,
            new FreelancerHandler(),
            new AutoSaveHandler(),
            new CleanerHandler(),
            new DebugHandler(),
            new UpdateTicketName(),
            new QuestionsHandler(),
            new TicketTranscriptHandler(),
            new QuoteHandler(),
            new TicketAnnouncementHandler(),
            new TicketCreatorHandler(),
            new TicketHandler());
    handlers.forEach(handler -> handler.register(jda, listenerManager));
    StarfishProvidersRegistry registry = new StarfishProvidersRegistry(languageHandler);
    CommandManager commandManager =
        new CommandManager(
                registry,
                languageHandler,
                new StarfishPermissionChecker(languageHandler, loader),
                jda,
                configuration.getListenerOptions())
            .parseAndRegisterAll(
                new CacheCommands(),
                new ChannelsCommands(),
                new DeveloperCommands(),
                new FreelancerCommands(),
                new InvoiceCommands(),
                new ModerationCommands(),
                new PermissionCommands(),
                new PortfolioCommands(),
                new SetCommands(),
                new TicketCommands());
    AddonLoader addonLoader =
        JavaAddonLoader.at(StarfishFiles.ADDONS)
            .handleNotLoaded(e -> fallback.process(e, "Addon could not be loaded"))
            .handle(fallback::process)
            .supply(
                addonInformation -> {
                  Logger logger = Logger.getLogger(addonInformation.getName());
                  logger.setParent(SimpleStarfish.log);
                  return logger;
                })
            .build();
    MemoryCache memoryCache = new MemoryCache().register(scheduler);
    SimpleStarfish starfish =
        new SimpleStarfish(
            configuration,
            discordConfiguration,
            fallback,
            memoryCache,
            connection,
            commandManager,
            listenerManager,
            new StarfishTicketManager(loader, configuration),
            handlers,
            addonLoader,
            scheduler,
            new Json(Mongo.GSON));
    Starfish.setInstance(starfish);
    starfish.getHandlers().forEach(StarfishHandler::onEnable);
    SimpleStarfish.log.info("Bot is ready to use");
  }

  @Override
  public @NonNull Gson getGson() {
    return Mongo.GSON;
  }
}
