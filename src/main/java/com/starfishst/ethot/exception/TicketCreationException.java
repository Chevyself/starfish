package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

/**
 * In case something goes wrong while trying to create a ticket
 *
 * @author Chevy
 * @version 1.0.0
 */
public class TicketCreationException extends SimpleException {

  public TicketCreationException(@NotNull String message, @NotNull Object... objects) {
    super(message, objects);
  }
}
