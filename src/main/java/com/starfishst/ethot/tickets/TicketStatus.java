package com.starfishst.ethot.tickets;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/** All tickets have an status that tells if you can do certain changes or not */
public enum TicketStatus {
  /** When the ticket is asking for details and is just being created */
  CREATING,
  /** When business is happening and many changes can be done */
  OPEN,
  /** When you don't need the ticket anymore */
  CLOSED,
  /** When the business has finished but you still want the ticket for changes */
  ARCHIVED,
  /** When a {@link com.starfishst.ethot.tickets.type.Product} is selling */
  SELLING;

  /**
   * Get a ticket status using a document. This is replaced with {@link
   * com.starfishst.ethot.tickets.loader.mongo.codec.TicketStatusCodec}
   *
   * @param document the document to get the status from
   * @return the status from the document
   */
  @NotNull
  @Deprecated
  public static TicketStatus fromDocument(@NotNull Document document) {
    try {
      return TicketStatus.valueOf(document.getString("status"));
    } catch (NullPointerException | IllegalArgumentException e) {
      throw e;
    }
  }
}
