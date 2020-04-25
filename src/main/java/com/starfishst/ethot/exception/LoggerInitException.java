package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;

/**
 * In case something goes wrong while trying to start the application logger
 *
 * @author Chevy
 * @version 1.0.0
 */
public class LoggerInitException extends SimpleRuntimeException {

  public LoggerInitException() {
    super("Logger could not be initialized");
  }
}
