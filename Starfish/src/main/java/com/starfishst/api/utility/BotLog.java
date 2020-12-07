package com.starfishst.api.utility;

import com.starfishst.api.Starfish;
import java.util.logging.Level;
import lombok.NonNull;
import me.googas.commons.builder.LogBuilder;

/** Extension of {@link LogBuilder} */
public class BotLog extends LogBuilder {

  public BotLog(@NonNull Level level, @NonNull StringBuffer buffer, Object initial) {
    super(level, buffer, initial);
  }

  public BotLog(@NonNull Level level, Object initial) {
    super(level, initial);
  }

  public BotLog(@NonNull Level level) {
    super(level);
  }

  public BotLog(Object initial) {
    super(initial);
  }

  /** Send the message to the Starfish bot logger */
  public void send() {
    this.send(Starfish.getLogger());
  }
}
