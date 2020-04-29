package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;

/**
 * Thrown when {@link com.starfishst.ethot.config.PunishmentsConfiguration} could not be initialized
 */
public class PunishmentsInitException extends SimpleRuntimeException {
  /** Create an instance */
  public PunishmentsInitException() {
    super("Punishments could not be initialized!");
  }
}
