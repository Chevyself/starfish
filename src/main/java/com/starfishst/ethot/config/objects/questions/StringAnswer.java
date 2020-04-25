package com.starfishst.ethot.config.objects.questions;

import org.jetbrains.annotations.NotNull;

/**
 * This is the simplest form of answer
 *
 * @author Chevy
 * @version 1.0.0
 */
public class StringAnswer implements Answer {

  @NotNull private final String answer;

  public StringAnswer(@NotNull String answer) {
    this.answer = answer;
  }

  @NotNull
  @Override
  public String getAnswer() {
    return answer;
  }
}
