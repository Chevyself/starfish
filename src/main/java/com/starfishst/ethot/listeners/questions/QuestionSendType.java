package com.starfishst.ethot.listeners.questions;

/** How should the questions be managed */
public enum QuestionSendType {
  /** Don't do nothing with the questions */
  NONE,
  /** Delete both the questions and the answer */
  DELETE,
  /** Change the message that contains the question */
  REPLACE
}
