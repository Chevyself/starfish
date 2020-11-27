package com.starfishst.api.events.user;

import com.starfishst.api.data.user.FreelancerRating;
import org.jetbrains.annotations.NotNull;

/** Called when the freelancer rating is unloaded */
public class FreelancerRatingUnloadedEvent extends FreelancerRatingEvent {

  /**
   * Create the event
   *
   * @param rating the rating involved in the event
   */
  public FreelancerRatingUnloadedEvent(@NotNull FreelancerRating rating) {
    super(rating);
  }
}
