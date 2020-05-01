package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

/** In case something goes wrong when a freelancer tries to join a ticket */
public class FreelancerJoinTicketException extends SimpleException {

  /**
   * Create an instance
   *
   * @param message the message to send
   */
  public FreelancerJoinTicketException(@NotNull String message) {
    super(message);
  }
}
