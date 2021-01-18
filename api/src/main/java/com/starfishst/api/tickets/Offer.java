package com.starfishst.api.tickets;

import lombok.NonNull;

/** An offer is sent to */
public interface Offer {

  /**
   * Get the id of the freelancer that made the offer
   *
   * @return the id of the freelancer
   */
  long getFreelancer();

  /**
   * Get the offer
   *
   * @return the offer as a string
   */
  @NonNull
  String getOffer();

  /**
   * Get the ticket id
   *
   * @return the ticket id of the offer
   */
  long getTicket();
}
