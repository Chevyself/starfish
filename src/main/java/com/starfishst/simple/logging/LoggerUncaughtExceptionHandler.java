package com.starfishst.simple.logging;

import com.starfishst.core.fallback.Fallback;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/** A uncaught exception handler that sends the message into a logger instance */
public class LoggerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

  /** The logger to send the throwable to */
  @NotNull private final Logger logger;

  /**
   * Create an instance
   *
   * @param logger the logger to use to send messages
   */
  public LoggerUncaughtExceptionHandler(@NotNull Logger logger) {
    this.logger = logger;
  }

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {
    Fallback.addError(throwable.getMessage());
    logger.log(Level.SEVERE, throwable.getMessage(), throwable);
  }
}
