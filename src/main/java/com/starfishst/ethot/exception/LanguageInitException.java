package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;
import org.jetbrains.annotations.NotNull;

/**
 * In case something goes wrong while trying to load 'lang.properties'
 *
 * @author Chevy
 * @version 1.0.0
 */
public class LanguageInitException extends SimpleRuntimeException {

  public LanguageInitException(@NotNull String message, @NotNull Object... objects) {
    super(message, objects);
  }
}
