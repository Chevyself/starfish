package com.starfishst.ethot.util;

import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.LoggerInitException;
import com.starfishst.simple.logging.LoggerFactory;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Static logger usage
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Console {

  /** The logger instance to log messages */
  @NotNull private static final Logger logger;

  static {
    try {
      System.out.println("Initializing logger");
      logger = LoggerFactory.getLogger(Main.class.getName(), Lang.get("LOG_FORMAT"), null);
    } catch (IOException e) {
      Errors.addError(e.getMessage());
      throw new LoggerInitException();
    }
  }

  /** Closes the logger and its handlers */
  public static void shutdown() {
    info("Closing logger handlers...");
    for (Handler handler : logger.getHandlers()) {
      handler.close();
    }
  }

  /**
   * Log a severe message {@link Logger#severe(String)}
   *
   * @param msg the message to log
   */
  @Deprecated
  public static void severe(String msg) {
    logger.severe(msg);
  }

  /**
   * Log a warning message {@link Logger#warning(String)}
   *
   * @param msg the message to log
   */
  @Deprecated
  public static void warning(String msg) {
    logger.warning(msg);
  }

  /**
   * Log an info message {@link Logger#info(String)}
   *
   * @param msg the message to log
   */
  public static void info(String msg) {
    logger.info(msg);
  }

  /**
   * Log a config message {@link Logger#config(String)}
   *
   * @param msg the message to log
   */
  @Deprecated
  public static void config(String msg) {
    logger.config(msg);
  }

  /**
   * Log a fine message {@link Logger#fine(String)}
   *
   * @param msg the message to log
   */
  @Deprecated
  public static void fine(String msg) {
    logger.fine(msg);
  }

  /**
   * Log a finer message {@link Logger#finer(String)}
   *
   * @param msg the message to log
   */
  @Deprecated
  public static void finer(String msg) {
    logger.finer(msg);
  }

  /**
   * Log a finest message {@link Logger#config(String)}
   *
   * @param msg the message to log
   */
  @Deprecated
  public static void finest(String msg) {
    logger.finest(msg);
  }

  /**
   * Log a throwable
   *
   * @param level the level to log it
   * @param thrown the throwable to log
   */
  public static void log(Level level, Throwable thrown) {
    logger.log(level, thrown.getMessage(), thrown);
  }

  /**
   * Get the logger that is being used in the console
   *
   * @return the logger being used in the console
   */
  @NotNull
  public static Logger getLogger() {
    return logger;
  }
}
