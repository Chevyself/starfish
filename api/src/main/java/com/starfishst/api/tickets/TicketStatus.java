package com.starfishst.api.tickets;

/**
 * This is the enum that shows the ticket status. All tickets have an status that tells if you can
 * do certain changes, actions or not
 */
public enum TicketStatus {

  /** When the ticket is being loaded by the ticket manager */
  LOADING,
  /** When the ticket is asking for details and is just being created */
  CREATING,
  /** When business is happening and many changes can be done */
  OPEN,
  /** When you don't need the ticket anymore */
  CLOSED,
  /** When the business has finished but you still want the ticket for changes */
  ARCHIVED,
  /** */
  SELLING
}
