package com.starfishst.bot;

import com.starfishst.bot.addons.AddonLoader;
import com.starfishst.bot.commands.AddCommands;
import com.starfishst.bot.commands.AddonsCommands;
import com.starfishst.bot.commands.BlacklistCommands;
import com.starfishst.bot.commands.CopyCommands;
import com.starfishst.bot.commands.DevCommands;
import com.starfishst.bot.commands.FreelancerCommands;
import com.starfishst.bot.commands.HelpCommands;
import com.starfishst.bot.commands.ModerationCommands;
import com.starfishst.bot.commands.RemoveCommands;
import com.starfishst.bot.commands.ScanCommand;
import com.starfishst.bot.commands.SetCommand;
import com.starfishst.bot.commands.TicketsCommand;
import com.starfishst.bot.commands.invoices.EnhancedInvoice;
import com.starfishst.bot.commands.invoices.InvoiceCommand;
import com.starfishst.bot.commands.provider.AllowedTicketCloserCheckerProvider;
import com.starfishst.bot.commands.provider.AllowedTicketManagerCheckerProvider;
import com.starfishst.bot.commands.provider.FreelancerProvider;
import com.starfishst.bot.commands.provider.FreelancerSenderProvider;
import com.starfishst.bot.config.BotInfoConfiguration;
import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.DiscordConfiguration;
import com.starfishst.bot.config.MongoConfiguration;
import com.starfishst.bot.config.adapters.CategoryAdapter;
import com.starfishst.bot.config.adapters.ColorAdapter;
import com.starfishst.bot.config.adapters.FeeAdapter;
import com.starfishst.bot.config.adapters.GuildAdapter;
import com.starfishst.bot.config.adapters.QuestionAdapter;
import com.starfishst.bot.config.adapters.ResponsiveMessageAdapter;
import com.starfishst.bot.config.adapters.RoleAdapter;
import com.starfishst.bot.config.adapters.TextChannelAdapter;
import com.starfishst.bot.config.adapters.TimeAdapter;
import com.starfishst.bot.config.adapters.UserAdapter;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.listeners.ConfigurationListener;
import com.starfishst.bot.listeners.ModerationListener;
import com.starfishst.bot.listeners.ResponsiveMessagesListener;
import com.starfishst.bot.listeners.TicketTranscriptListener;
import com.starfishst.bot.listeners.WelcomeListener;
import com.starfishst.bot.listeners.questions.QuestionTicketListener;
import com.starfishst.bot.objects.invoicing.Fee;
import com.starfishst.bot.objects.invoicing.Payments;
import com.starfishst.bot.objects.questions.Question;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.tasks.AutoSave;
import com.starfishst.bot.tasks.InactiveCheck;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.loader.mongo.MongoTicketLoader;
import com.starfishst.bot.util.Console;
import com.starfishst.commands.CommandManager;
import com.starfishst.commands.commands.FallbackCommands;
import com.starfishst.commands.providers.registry.ImplProvidersRegistry;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.Strings;
import com.starfishst.core.utils.Validate;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.core.utils.time.Time;
import com.starfishst.simple.config.JsonConfiguration;
import com.starfishst.simple.gson.GsonProvider;
import com.starfishst.simple.logging.LoggerUncaughtExceptionHandler;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main class of the bot! This bot allows you to make your discord server to become a service!
 * Allow people to create tickets you can also have freelancers and sell stuff!
 */
public class Main {

  /** The tasks that are running in the bot */
  @NotNull private static final List<TimerTask> tasks = new ArrayList<>();
  /** The config instance for multipurpose */
  @Nullable private static Configuration configuration;
  /** The discord config instance for multipurpose */
  @Nullable private static DiscordConfiguration discConfiguration;
  /** The JDA instance for discord manipulation */
  @Nullable private static JDA jda;
  /** The addon loader instance */
  @Nullable private static AddonLoader loader;
  /** The ticket manager for ticket handling */
  @Nullable private static TicketManager manager;
  /** The command manager for command listening */
  @Nullable private static CommandManager commandManager;
  /** If the ticket is stopping it will be true */
  private static boolean stopping = false;

  /**
   * Main java method
   *
   * @param args there's no args which can be used to start the bot
   */
  public static void main(String[] args) {
    checkJavaMethod();
    System.out.println("This messages wont be saved in the log file");
    Console.info("From now the messages will be saved in the log file");
    printInformation();
    Thread.setDefaultUncaughtExceptionHandler(
        new LoggerUncaughtExceptionHandler(Console.getLogger()));
    setupConfiguration();
    setupJDA();
    setupAddons();
    setupDiscordConfig();
    setupTicketManager();
    setupCommands();
    startTasks();

    if (!Fallback.isEmpty()) {
      createFallback(
          "There was some errors while enabling the bot \n Run 'errors' in console to get a list of them or 'exit' to end the program");
    }
    Console.info("Bot is up and running");
  }

  /** Loads the addons */
  private static void setupAddons() {
    Console.info("Loading addons");
    File addonsDirectory =
        new File(CoreFiles.validatePath(CoreFiles.currentDirectory() + "/addons"));
    try {
      loader = new AddonLoader(addonsDirectory);
      loader.load();
    } catch (IOException e) {
      Fallback.addError("Addons could not be loaded");
      e.printStackTrace();
    }
  }

  /** Checks that the bot is running in a java 8 environment */
  private static void checkJavaMethod() {
    String javaVer = System.getProperty("java.version");
    if (!javaVer.startsWith("1.8")) {
      System.out.println("Please run the bot in java 8. You are running in java " + javaVer);
      System.exit(0);
    }
  }

  /** Prints the bot build information */
  private static void printInformation() {
    try {
      BotInfoConfiguration botInfoConfiguration = new BotInfoConfiguration();
      Console.info("Bot build: " + botInfoConfiguration.getGitHash());
    } catch (IOException e) {
      Console.info("Bot info could not be provided");
    }
  }

  /** Starts the tasks that will be running in the bot */
  public static void startTasks() {
    Console.info("Creating tasks");
    tasks.add(new InactiveCheck());
    if (configuration != null && configuration.getAutoSave().isEnable()) {
      tasks.add(new AutoSave(configuration.getAutoSave().getToSave()));
    }
  }

  /**
   * Setups the main configuration 'config.json'
   *
   * <p>Adds the adapters to {@link GsonProvider}, loads the resource or gets the file and loads it
   * as a java object
   *
   * <p>It is very important to not delete the default keys because they could load as 'null' and
   * generate errors
   */
  private static void setupConfiguration() {
    try {
      Console.info("Setting up configuration");
      GsonProvider.addAdapter(Category.class, new CategoryAdapter());
      GsonProvider.addAdapter(Color.class, new ColorAdapter());
      GsonProvider.addAdapter(Fee.class, new FeeAdapter());
      GsonProvider.addAdapter(Guild.class, new GuildAdapter());
      GsonProvider.addAdapter(Question.class, new QuestionAdapter());
      GsonProvider.addAdapter(ResponsiveMessage.class, new ResponsiveMessageAdapter());
      GsonProvider.addAdapter(Role.class, new RoleAdapter());
      GsonProvider.addAdapter(TextChannel.class, new TextChannelAdapter());
      GsonProvider.addAdapter(Time.class, new TimeAdapter());
      GsonProvider.addAdapter(User.class, new UserAdapter());
      GsonProvider.refresh();
      configuration = JsonConfiguration.getInstance("config.json", Configuration.class);
      Console.info("Configuration setup has been completed");
    } catch (IOException e) {
      Console.log(Level.SEVERE, e);
      Fallback.addError(e.getMessage());
    }
  }

  /**
   * Setups JDA (Discord)
   *
   * <p>If there was any kind of error while loading 'config.json' it will also be not able to load
   */
  private static void setupJDA() {
    if (configuration != null) {
      try {
        Console.info("Connecting to discord...");
        jda =
            JDABuilder.create(configuration.getToken(), Arrays.asList(GatewayIntent.values()))
                .setEventManager(new AnnotatedEventManager())
                .addEventListeners(new ModerationListener())
                .addEventListeners(new ResponsiveMessagesListener(configuration))
                .addEventListeners(new QuestionTicketListener())
                .addEventListeners(new TicketTranscriptListener())
                .addEventListeners(new ConfigurationListener())
                .addEventListeners(new WelcomeListener())
                .build();
        Console.info("Waiting for Discord connection...");
        long millis = 0;
        while (jda.getStatus() != JDA.Status.CONNECTED) {
          Thread.sleep(1);
          millis++;
        }
        Console.info("Discord took " + Time.fromMillis(millis).toEffectiveString() + " to connect");

      } catch (LoginException e) {
        Fallback.addError("Discord token is wrong");
        Console.log(Level.SEVERE, e);
      } catch (InterruptedException e) {
        Fallback.addError(e.getMessage());
        Console.log(Level.SEVERE, e);
      }
    } else {
      Fallback.addError(
          "JDA Could not be initialized because there was an error while setting up configuration");
    }
  }

  /**
   * After loading up Discord we can setup de 'discord.json' configuration because JDA is needed in
   * order to get roles, categories, users, etc...
   */
  private static void setupDiscordConfig() {
    try {
      if (jda != null) {
        Console.info("Setting up Discord configuration");
        discConfiguration =
            JsonConfiguration.getInstance("discord.json", DiscordConfiguration.class);
      } else {
        Fallback.addError("Discord configuration could not be setup as there is no JDA connection");
      }
    } catch (IOException e) {
      Console.log(Level.SEVERE, e);
      Fallback.addError(e.getMessage());
    }
  }

  /** Setups the ticket manager. It needs of both JDA and configurations */
  private static void setupTicketManager() {
    if (configuration != null) {
      Console.info("Setting up Ticket Manager");
      MongoConfiguration mongo = configuration.getMongo();
      try {
        manager = new TicketManager(new MongoTicketLoader(mongo.getUri(), mongo.getDatabase()));
      } catch (Throwable e) {
        Console.log(Level.SEVERE, e);
        Fallback.addError("Ticket manager could not be setup: " + e.getMessage());
      }
    } else {
      Fallback.addError(
          "Ticket Manager could not be setup because there's some errors in the configuration");
    }
  }

  /**
   * Sets up the commands. We do after everything else is setup so you cannot use them before
   *
   * <p>It needs both JDA and 'config.json'
   */
  private static void setupCommands() {
    Console.info("Setting up commands");
    if (jda != null && configuration != null) {
      ImplProvidersRegistry registry = ImplProvidersRegistry.getInstance();
      registry.addProvider(new AllowedTicketManagerCheckerProvider());
      registry.addProvider(new AllowedTicketCloserCheckerProvider());
      registry.addProvider(new FreelancerProvider());
      registry.addProvider(new FreelancerSenderProvider());
      commandManager =
          new CommandManager(
              jda, configuration.getPrefix(), configuration.getCommands(), Lang.getProvider());
      commandManager.registerCommand(new AddCommands());
      commandManager.registerCommand(new DevCommands());
      commandManager.registerCommand(new FreelancerCommands());
      commandManager.registerCommand(new HelpCommands());
      commandManager.registerCommand(new ModerationCommands());
      commandManager.registerCommand(new BlacklistCommands());
      commandManager.registerCommand(new RemoveCommands());
      commandManager.registerCommand(new SetCommand());
      commandManager.registerCommand(new TicketsCommand());
      commandManager.registerCommand(new FallbackCommands());
      commandManager.registerCommand(new CopyCommands());
      commandManager.registerCommand(new ScanCommand(TicketManager.getInstance()));
      if (loader != null) {
        commandManager.registerCommand(new AddonsCommands(loader));
      }
      if (configuration.getPayments().isEnable()) {
        try {
          Payments.initialize();
          commandManager.registerCommand(new EnhancedInvoice());
        } catch (IOException e) {
          Fallback.addError(e.getMessage());
        }
      } else {
        commandManager.registerCommand(new InvoiceCommand());
      }

    } else {
      Fallback.addError(
          "Could not setup commands because JDA or the configuration were not setup properly");
    }
  }

  /**
   * In case any error happened a fallback will be created in order to help the user solve them
   *
   * @param message a message of why was the fallback created
   */
  private static void createFallback(@NotNull String message) {
    Console.info(message);
    Scanner in = new Scanner(System.in);
    while (true) {
      if (in.hasNext()) {
        String next = in.next();
        if (next.equalsIgnoreCase("errors")) {
          Console.info(buildErrorsMessage().toString());
        } else if (next.equalsIgnoreCase("exit")) {
          Console.info("In case you could not solve something contact me in Discord Chevy#0942");
          Console.shutdown();
          System.exit(0);
          break;
        } else {
          System.out.println("Unknown use 'errors' or 'exit'");
        }
      }
    }
  }

  /**
   * Builds the message sent in the fallback when the user uses the command 'errors'
   *
   * @return the built message
   */
  @NotNull
  private static StringBuilder buildErrorsMessage() {
    StringBuilder builder = Strings.getBuilder();
    builder.append("Errors: \n");
    Fallback.getErrors()
        .getList()
        .forEach(error -> builder.append("> ").append(error).append("\n"));
    return builder;
  }

  /**
   * Saves the bot configuration 'lang.properties', 'config.json', 'discord.json'
   *
   * @throws IOException when handling files goes wrong
   */
  public static void save() throws IOException {
    Console.info("Saving configurations...");
    Lang.save();
    getConfiguration().save();
    getDiscordConfiguration().save();
    // PunishmentsConfiguration.getInstance().save();
  }

  /** Stops the bot while saving the config */
  public static void stop() {
    try {
      stopping = true;
      Console.info("Saving modules");
      if (loader != null) {
        loader.unload();
      }
      tasks.forEach(TimerTask::cancel);
      Console.info("Cleaning cache");
      Cache.getCache().forEach(Catchable::onRemove);
      Cache.getCache().clear();
      save();
      if (jda != null) {
        Console.info("Closing jda");
        jda.shutdown();
      }
      if (manager != null) {
        Console.info("Closing loader");
        manager.getLoader().close();
      }
    } catch (Throwable e) {
      Console.log(Level.SEVERE, e);
      Fallback.addError(e.getMessage());
    }
    if (!Fallback.isEmpty()) {
      createFallback(
          "There was some errors while disabling the bot \n Run 'errors' in console to get a list of them or 'exit' to end the program");
    } else {
      Console.info("Shutting down logger");
      Console.shutdown();
      System.exit(0);
    }
  }

  /**
   * Checks if the configuration already has been loaded
   *
   * @return true if the configuration has been loaded
   */
  public static boolean hasConfiguration() {
    return configuration != null;
  }

  /**
   * Get the main configuration 'config.json'
   *
   * @return the config java object
   */
  @NotNull
  public static Configuration getConfiguration() {
    return Validate.notNull(configuration, "Configuration was not setup properly");
  }

  /**
   * Get the ticket manager and manipulate tickets/freelancers. Use {@link
   * TicketManager#getInstance()}
   *
   * @return the ticket manager
   */
  @Deprecated
  @NotNull
  public static TicketManager getManager() {
    return Validate.notNull(manager, "Ticket Manager was not setup properly");
  }

  /**
   * Get the discord configuration 'discord.json'. Use {@link TicketManager#getInstance()}
   *
   * @return the config java object
   */
  @NotNull
  public static DiscordConfiguration getDiscordConfiguration() {
    return Validate.notNull(discConfiguration, "Discord configuration was not setup properly");
  }

  /**
   * Get the connection with Discord
   *
   * @return jda core of discord
   */
  @NotNull
  public static JDA getJda() {
    return Validate.notNull(jda, "JDA was not setup properly");
  }

  /**
   * Get the command manager for getting commands or dispatching them
   *
   * @return the command manager
   */
  @NotNull
  public static CommandManager getCommandManager() {
    return Validate.notNull(commandManager, "Commands were not setup properly");
  }

  /**
   * Get if the bot is stopping
   *
   * @return true if stopping
   */
  public static boolean isStopping() {
    return stopping;
  }

  /**
   * Get the tasks that the bot is running
   *
   * @return the list of tasks
   */
  @NotNull
  public static List<TimerTask> getTasks() {
    return tasks;
  }
}
