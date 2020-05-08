package com.starfishst.bot.exception;

import com.starfishst.bot.legacy.CopyLegacyProcess;
import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

/** Thrown when {@link CopyLegacyProcess#start()} has an error */
public class CopyProcessException extends SimpleException {

  /**
   * Throw the exception
   *
   * @param message the message to send in the exception
   */
  public CopyProcessException(@NotNull String message) {
    super(message);
  }
}
