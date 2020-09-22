package com.starfishst.bot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;

/**
 * Thrown when {@link com.starfishst.bot.oldconfig.questions.QuestionsHandler} could not be initialized
 */
public class QuestionsInitException extends SimpleRuntimeException {

  /** Create an instance */
  public QuestionsInitException() {
    super("Questions could not be initialized!");
  }
}
