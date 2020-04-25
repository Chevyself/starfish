package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

/** In case something goes wrong while trying to edit discord stuff */
public class DiscordManipulationException extends SimpleException {

  /**
   * Create an instance
   *
   * @param message the message to send
   * @param objects to change the placeholders
   */
  public DiscordManipulationException(@NotNull String message, @NotNull Object... objects) {
    super(message, objects);
  }
}
