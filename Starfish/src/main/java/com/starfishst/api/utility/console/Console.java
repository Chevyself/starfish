package com.starfishst.api.utility.console;

import com.starfishst.bot.Starfish;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.googas.commons.CoreFiles;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.time.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An easy way to log messages */
public class Console {

  /** The logger instance to log messages */
  @NotNull private static final Logger logger = Logger.getLogger(Starfish.class.getName());

  static {
    CustomFormatter formatter =
        new CustomFormatter(
            "[%hour%:%minute%:%second% - %day%/%month%/%year% %level%] %message% %stack% \n");
    logger.setUseParentHandlers(false);
    try {
      logger.addHandler(Console.getFileHandler(formatter, null));
    } catch (IOException e) {
      Fallback.addError("IOException: File Handler could not be initialized");
      Console.exception(e, "IOException: File Handler could not be initialized");
    }
    logger.addHandler(Console.getConsoleHandler(formatter));
  }

  /**
   * Get a file handler which creates files in /logs
   *
   * @param formatter the formatter to use in the handler
   * @param url the path to the file
   * @return the handler created
   * @throws IOException in case the /logs directory could not be created
   */
  @NotNull
  public static FileHandler getFileHandler(@NotNull Formatter formatter, @Nullable String url)
      throws IOException {
    File directory = CoreFiles.directoryOrCreate(CoreFiles.currentDirectory() + "/logs/");
    LocalDateTime date = TimeUtils.getLocalDateFromMillis(System.currentTimeMillis());
    FileHandler handler;
    if (url != null) {
      handler = new FileHandler(url, true);
    } else {
      handler =
          new FileHandler(
              directory.toPath().toString()
                  + File.separator
                  + date.getMonthValue()
                  + "-"
                  + date.getDayOfMonth()
                  + "-"
                  + date.getYear()
                  + " @ "
                  + date.getHour()
                  + "-"
                  + date.getMinute()
                  + ".txt");
    }
    handler.setFormatter(formatter);
    handler.setLevel(Level.FINEST);
    return handler;
  }

  /**
   * Get a new console handler
   *
   * @param formatter the formatter to use in the handler
   * @return the handler formatted
   */
  @NotNull
  public static ConsoleHandler getConsoleHandler(@NotNull Formatter formatter) {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(formatter);
    handler.setLevel(Level.FINEST);
    return handler;
  }

  /**
   * Log a message in the logger
   *
   * @param level the level of the message
   * @param msg the message to log
   */
  public static void log(@NotNull Level level, @NotNull String msg) {
    logger.log(level, msg);
  }

  /**
   * A debug message
   *
   * @param message an information message
   */
  public static void debug(@NotNull String message) {
    Console.log(Level.FINE, message);
  }

  /**
   * A debug message
   *
   * @param message an information message
   */
  public static void info(@NotNull String message) {
    Console.log(Level.INFO, message);
  }

  /**
   * An exception message. This will add automatically the exception to the fallback
   *
   * @param throwable the exception that was thrown
   * @param message an information message
   */
  public static void exception(@NotNull Throwable throwable, @NotNull String message) {
    Fallback.addError(message);
    logger.log(Level.SEVERE, message, throwable);
  }

  /**
   * An exception message. This will add automatically the exception to the fallback
   *
   * @param message an information message
   */
  public static void exception(@NotNull String message) {
    Fallback.addError(message);
    logger.log(Level.SEVERE, message);
  }

  /**
   * Get the logger that the console is using
   *
   * @return the logger
   */
  @NotNull
  public static Logger getLogger() {
    return logger;
  }

  /** Sets the console to debug */
  public static void setDebug() {
    for (Handler handler : logger.getHandlers()) {
      handler.setLevel(Level.FINEST);
    }
  }
}
