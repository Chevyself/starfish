package com.starfishst.bot.handlers.questions;

import lombok.NonNull;

/**
 * This is actually not a question but send a information message to the ticket that is being
 * created
 */
public class QuestionInformation extends Question {

  /**
   * Create a question
   *
   * @param title the title of the question
   * @param simple the simple of the question
   * @param description the description of the question
   * @param limit the limit of the question
   */
  public QuestionInformation(
      @NonNull String title, @NonNull String simple, @NonNull String description, int limit) {
    super(title, simple, description, limit);
  }
}
