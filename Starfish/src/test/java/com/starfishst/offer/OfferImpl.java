package com.starfishst.offer;

import com.starfishst.api.tickets.Offer;
import lombok.NonNull;

public class OfferImpl implements Offer {

  private final long freelancer;

  @NonNull private final String offer;

  private final long ticket;

  public OfferImpl(long freelancer, @NonNull String offer, long ticket) {
    this.freelancer = freelancer;
    this.offer = offer;
    this.ticket = ticket;
  }

  /**
   * Get the id of the freelancer that made the offer
   *
   * @return the id of the freelancer
   */
  @Override
  public long getFreelancer() {
    return 0;
  }

  /**
   * Get the offer
   *
   * @return the offer as a string
   */
  @Override
  public @NonNull String getOffer() {
    return null;
  }

  /**
   * Get the ticket id
   *
   * @return the ticket id of the offer
   */
  @Override
  public long getTicket() {
    return 0;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof OfferImpl)) return false;

    OfferImpl offer1 = (OfferImpl) object;

    if (freelancer != offer1.freelancer) return false;
    if (ticket != offer1.ticket) return false;
    return offer.equals(offer1.offer);
  }

  @Override
  public int hashCode() {
    int result = (int) (freelancer ^ (freelancer >>> 32));
    result = 31 * result + offer.hashCode();
    result = 31 * result + (int) (ticket ^ (ticket >>> 32));
    return result;
  }
}
