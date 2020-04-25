package com.starfishst.ethot.objects.responsive;

/** To identify different responsive messages */
public enum ResponsiveMessageType {

  /** The message waiting for the review of a freelancer */
  REVIEW,
  /** The message waiting for the creation of a ticket */
  TICKET_CREATOR,
  /** The message waiting for a customer to react */
  ORDER,
  /** The message of an offer waiting for a freelancer */
  OFFER,
  /** The message asking if a ticket should be archived */
  ARCHIVE,
  /** The message waiting for active members to react */
  INACTIVE_CHECK,
  /** The message listening for customers to create tickets */
  TICKET_PANEL,
  /** The message waiting for customer to buy a product */
  PRODUCT
}
