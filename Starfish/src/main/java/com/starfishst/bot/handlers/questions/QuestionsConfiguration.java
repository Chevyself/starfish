package com.starfishst.bot.handlers.questions;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/** A configuration file for questions */
public class QuestionsConfiguration {

  /** The list of questions for ticket creation */
  @NonNull private final List<Question> questions;

  /** Create an instance using gson */
  @Deprecated
  public QuestionsConfiguration() {
    this.questions = new ArrayList<>();
  }

  /**
   * Get the list of questions
   *
   * @return the list of questions
   */
  @NonNull
  public List<Question> getQuestions() {
    return questions;
  }
}
