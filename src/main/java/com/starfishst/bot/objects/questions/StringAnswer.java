package com.starfishst.bot.objects.questions;

import org.jetbrains.annotations.NotNull;

/** This is the simplest form of answer */
public class StringAnswer implements Answer {

  /** The answer */
  @NotNull private final String answer;

  /**
   * Create an instance
   *
   * @param answer the answer
   */
  public StringAnswer(@NotNull String answer) {
    this.answer = answer;
  }

  @NotNull
  @Override
  public String getAnswer() {
    return answer;
  }
}
