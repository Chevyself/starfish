package com.starfishst.api.exception;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.core.exceptions.type.SimpleException;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import lombok.NonNull;

/** An implementation for {@link SimpleException} */
public class StarfishException extends SimpleException {

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
  public EmbedQuery toQuery(@NonNull BotUser user) {
    return Messages.build(this.getMessage(), ResultType.ERROR, user);
  }
}
