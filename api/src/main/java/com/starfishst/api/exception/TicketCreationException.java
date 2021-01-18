package com.starfishst.api.exception;

import lombok.NonNull;

/** An exception thrown when a ticket cannot be created */
public class TicketCreationException extends StarfishException {

  /**
   * Create the exception
   *
   * @param message why was the exception thrown
   */
  public TicketCreationException(@NonNull String message) {
    super(message);
  }
}
