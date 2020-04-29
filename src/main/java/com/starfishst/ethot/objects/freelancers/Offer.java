package com.starfishst.ethot.objects.freelancers;

import com.starfishst.ethot.objects.responsive.type.quotes.OfferAcceptResponsiveMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the offer that a {@link Freelancer} can send to a {@link
 * com.starfishst.ethot.tickets.type.Quote}
 */
public class Offer {

  /** The freelancer sending the offer */
  @NotNull private final Freelancer freelancer;
  /** The offer as a string */
  @NotNull private final String offer;
  /** the message to accept the offer */
  @Nullable private final OfferAcceptResponsiveMessage message;

  /**
   * Create an instance
   *
   * @param freelancer the freelancer sending the offer
   * @param offer the offer given by the freelancer
   * @param message the message to accept the offer
   */
  public Offer(
      @NotNull Freelancer freelancer,
      @NotNull String offer,
      @Nullable OfferAcceptResponsiveMessage message) {
    this.freelancer = freelancer;
    this.offer = offer;
    this.message = message;
  }

  /**
   * Get the freelancer that made the offer
   *
   * @return the freelancer that made the offer
   */
  @NotNull
  public Freelancer getFreelancer() {
    return freelancer;
  }

  /**
   * Get the offer given by the freelancer
   *
   * @return the offer as string
   */
  @NotNull
  public String getOffer() {
    return offer;
  }

  /**
   * Get the message to accept the offer
   *
   * @return the message to accept the offer
   */
  @Nullable
  public OfferAcceptResponsiveMessage getMessage() {
    return message;
  }
}
