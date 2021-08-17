package com.starfishst.api.exception;

import com.starfishst.api.utility.Messages;
import lombok.NonNull;
import me.googas.commands.exceptions.type.StarboxException;
import me.googas.starbox.builders.MapBuilder;
import net.dv8tion.jda.api.entities.Message;

/** An implementation for {@link me.googas.commands.exceptions.type.StarboxException} */
public class StarfishException extends StarboxException {

  /**
   * Create the exception
   *
   * @param message a message explaining what caused the exception
   */
  public StarfishException(@NonNull String message) {
    super(message);
  }

  /**
   * Create the exception
   *
   * @param message the message to explain why the exception was caused
   * @param cause the exception that caused this one
   */
  public StarfishException(@NonNull String message, @NonNull Throwable cause) {
    super(message, cause);
  }

  /**
   * Get the query from an exception
   *
   * @return the exception as a message
   */
  @NonNull
  public Message toMessage() {
    return Messages.of("exception").build(MapBuilder.of("message", this.getMessage()).build());
  }
}
