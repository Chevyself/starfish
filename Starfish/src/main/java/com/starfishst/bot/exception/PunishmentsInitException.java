package com.starfishst.bot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;

/**
 * Thrown when {@link com.starfishst.bot.oldconfig.PunishmentsConfiguration} could not be initialized
 */
public class PunishmentsInitException extends SimpleRuntimeException {
  /** Create an instance */
  public PunishmentsInitException() {
    super("Punishments could not be initialized!");
  }
}
