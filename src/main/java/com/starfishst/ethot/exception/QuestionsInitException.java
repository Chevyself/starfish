package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;

/**
 * Thrown when {@link com.starfishst.ethot.config.questions.QuestionsHandler} could not be
 * initialized
 */
public class QuestionsInitException extends SimpleRuntimeException {

  /** Create an instance */
  public QuestionsInitException() {
    super("Questions could not be initialized!");
  }
}
