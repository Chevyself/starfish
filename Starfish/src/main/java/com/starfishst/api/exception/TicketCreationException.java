package com.starfishst.api.exception;

import org.jetbrains.annotations.NotNull;

/** An exception thrown when a ticket cannot be created */
public class TicketCreationException extends StarfishException {

  /**
   * Create the exception
   *
   * @param message why was the exception thrown
   */
  public TicketCreationException(@NotNull String message) {
    super(message);
  }
}
