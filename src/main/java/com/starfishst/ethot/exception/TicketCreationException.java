package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

/** In case something goes wrong while trying to create a ticket */
public class TicketCreationException extends SimpleException {

  /**
   * Create an instance
   *
   * @param message the message to send
   */
  public TicketCreationException(@NotNull String message) {
    super(message);
  }
}
