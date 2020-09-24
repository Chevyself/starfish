package com.starfishst.api.data.tickets.exception;

import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

/** An exception thrown when a ticket cannot be created */
public class TicketCreationException extends SimpleException {

  /**
   * Create the exception
   *
   * @param message why was the exception thrown
   */
  public TicketCreationException(@NotNull String message) {
    super(message);
  }
}
