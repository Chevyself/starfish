package com.starfishst.api;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.experimental.Delegate;

/**
 * The most simple implementation of fallback that the processing for an exception is just printing
 * its stacktrace and adding the message of it to the errors
 */
public class Fallback {

  @Delegate @NonNull private final List<String> errors = new ArrayList<>();

  public void process(Throwable exception) {
    if (exception != null) this.process(exception, exception.getMessage());
  }

  public void process(Throwable exception, String message) {
    exception.printStackTrace();
    if (message != null) this.add(message);
  }

  @NonNull
  public  List<String> getErrors() {
    return this.errors;
  }
}