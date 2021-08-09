package com.starfishst.bot;

import com.starfishst.api.Fallback;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class StarfishFallback extends Fallback {

  @Getter @Setter @NonNull private Logger log;

  public StarfishFallback(@NonNull Logger log) {
    this.log = log;
  }

  @Override
  public void process(Throwable throwable) {
    this.process(throwable, null);
  }

  @Override
  public void process(Throwable throwable, String s) {
    if (throwable != null) {
      this.log.log(Level.SEVERE, throwable, () -> s == null ? "" : s);
      this.getErrors().add(s == null ? throwable.getMessage() : s);
    }
  }
}
