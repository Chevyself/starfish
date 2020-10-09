package com.starfishst.api.data.tickets;

import org.jetbrains.annotations.NotNull;

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
  @NotNull
  String getOffer();

  /**
   * Get the ticket id
   *
   * @return the ticket id of the offer
   */
  long getTicket();
}
