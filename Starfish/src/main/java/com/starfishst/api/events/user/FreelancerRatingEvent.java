package com.starfishst.api.events.user;

import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.api.events.StarfishEvent;
import org.jetbrains.annotations.NotNull;

/** An event that has a freelancer rating involved */
public class FreelancerRatingEvent implements StarfishEvent {

  /** The rating involved in the event */
  @NotNull private final FreelancerRating rating;

  /**
   * Create the event
   *
   * @param rating the rating involved in the event
   */
  public FreelancerRatingEvent(@NotNull FreelancerRating rating) {
    this.rating = rating;
  }

  /**
   * Get the freelancer rating involved in the event
   *
   * @return the rating involved in the event
   */
  @NotNull
  public FreelancerRating getRating() {
    return rating;
  }
}
