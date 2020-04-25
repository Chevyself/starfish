package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

/**
 * In case something goes wrong while trying to edit discord stuff
 *
 * @author Chevy
 * @version 1.0.0
 */
public class DiscordManipulationException extends SimpleException {

  public DiscordManipulationException(@NotNull String message, @NotNull Object... objects) {
    super(message, objects);
  }
}
