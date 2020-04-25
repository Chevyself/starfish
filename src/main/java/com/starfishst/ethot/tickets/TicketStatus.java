package com.starfishst.ethot.tickets;

import com.starfishst.core.utils.Errors;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public enum TicketStatus {
  CREATING,
  OPEN,
  CLOSED,
  ARCHIVED,
  SELLING;

  @NotNull
  public static TicketStatus fromDocument(@NotNull Document document) {
    try {
      return TicketStatus.valueOf(document.getString("status"));
    } catch (NullPointerException | IllegalArgumentException e) {
      Errors.addError(e.getMessage());
      throw e;
    }
  }
}
