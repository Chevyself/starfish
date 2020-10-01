package com.starfishst.api.exception;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedQuery;
import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link SimpleException} */
public class StarfishException extends SimpleException {

  /**
   * Create the exception
   *
   * @param message a message explaining what caused the exception
   */
  public StarfishException(@NotNull String message) {
    super(message);
  }

  /**
   * Create the exception
   *
   * @param message the message to explain why the exception was caused
   * @param cause the exception that caused this one
   */
  public StarfishException(@NotNull String message, @NotNull Throwable cause) {
    super(message, cause);
  }

  /**
   * Get the query from an exception
   *
   * @param user the user that will read the exception
   * @return the exception as a query
   */
  @NotNull
  public EmbedQuery toQuery(@NotNull BotUser user) {
    return Messages.build(this.getMessage(), ResultType.ERROR, user);
  }
}
