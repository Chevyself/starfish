package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleException;
import org.jetbrains.annotations.NotNull;

public class FreelancerJoinTicketException extends SimpleException {

  public FreelancerJoinTicketException(@NotNull String message, @NotNull Object... objects) {
    super(message, objects);
  }
}
