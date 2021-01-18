package com.starfishst.api.events.user;

import com.starfishst.api.events.StarfishEvent;
import com.starfishst.api.user.FreelancerRating;
import lombok.Getter;
import lombok.NonNull;

/** An event that has a freelancer rating involved */
public class FreelancerRatingEvent implements StarfishEvent {

  @NonNull @Getter private final FreelancerRating rating;

  /**
   * Create the event
   *
   * @param rating the rating involved in the event
   */
  public FreelancerRatingEvent(@NonNull FreelancerRating rating) {
    this.rating = rating;
  }
}
