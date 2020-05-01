package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;
import org.jetbrains.annotations.NotNull;

/** In case something goes wrong while trying to load 'lang.properties' */
public class LanguageInitException extends SimpleRuntimeException {

  /**
   * Create an instance
   *
   * @param message the message to send
   */
  public LanguageInitException(@NotNull String message) {
    super(message);
  }
}
