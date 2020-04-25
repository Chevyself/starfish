package com.starfishst.ethot.config.objects.freelancers;

import com.starfishst.ethot.config.objects.responsive.type.quotes.OfferAcceptResponsiveMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the offer that a {@link Freelancer} can send to a {@link
 * com.starfishst.ethot.tickets.type.Quote}
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Offer {

  @NotNull private final Freelancer freelancer;
  @NotNull private final String offer;
  @Nullable private final OfferAcceptResponsiveMessage message;

  public Offer(
      @NotNull Freelancer freelancer,
      @NotNull String offer,
      @Nullable OfferAcceptResponsiveMessage message) {
    this.freelancer = freelancer;
    this.offer = offer;
    this.message = message;
  }

  @NotNull
  public Freelancer getFreelancer() {
    return freelancer;
  }

  @NotNull
  public String getOffer() {
    return offer;
  }

  @Nullable
  public OfferAcceptResponsiveMessage getMessage() {
    return message;
  }
}
