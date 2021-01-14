package com.starfishst.api.events.user;

import com.starfishst.api.user.FreelancerRating;
import lombok.NonNull;

/** Called when the freelancer rating is unloaded */
public class FreelancerRatingUnloadedEvent extends FreelancerRatingEvent {

  /**
   * Create the event
   *
   * @param rating the rating involved in the event
   */
  public FreelancerRatingUnloadedEvent(@NonNull FreelancerRating rating) {
    super(rating);
  }
}
