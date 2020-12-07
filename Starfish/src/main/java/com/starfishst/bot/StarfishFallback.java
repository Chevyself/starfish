package com.starfishst.bot;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.fallback.Fallback;

public class StarfishFallback implements Fallback {

  @NonNull private final List<String> errors;
  @Getter @Setter @NonNull private Logger log;

  public StarfishFallback(@NonNull Logger log, @NonNull List<String> errors) {
    this.log = log;
    this.errors = errors;
  }

  @Override
  public void process(Throwable throwable) {
    this.process(throwable, null);
  }

  @Override
  public void process(Throwable throwable, String s) {
    log.log(Level.SEVERE, throwable, () -> s);
  }

  @Override
  public @NonNull List<String> getErrors() {
    return errors;
  }
}
