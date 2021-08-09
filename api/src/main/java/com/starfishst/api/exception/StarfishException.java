package com.starfishst.api.exception;

import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import lombok.NonNull;
import me.googas.commands.exceptions.type.StarboxException;
import me.googas.commands.jda.result.ResultType;
import net.dv8tion.jda.api.MessageBuilder;
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
   * @param user the user that will read the exception
   * @return the exception as a query
   */
  @NonNull
  public MessageBuilder toQuery(@NonNull BotUser user) {
    return new MessageBuilder(Messages.build(this.getMessage(), ResultType.ERROR, user));
  }
}
