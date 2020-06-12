package com.starfishst.bot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;

/** In case something goes wrong while trying to start the application logger */
public class LoggerInitException extends SimpleRuntimeException {

  /** Create an instance */
  public LoggerInitException() {
    super("Logger could not be initialized");
  }
}
