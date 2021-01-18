package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.user.FreelancerRatingUnloadedEvent;
import com.starfishst.api.user.FreelancerRating;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.time.Time;

/** Implementation of freelancer rating */
public class StarfishFreelancerRating implements FreelancerRating {

  /** The id of the freelancer that owns this */
  @Getter private final long id;

  /** The map that contains the rating of the freelancer */
  @Getter @NonNull private final Map<Long, Integer> map;

  /**
   * Create the freelancer rating
   *
   * @param id the id of the freelancer
   * @param map the map that contains the ratings
   */
  public StarfishFreelancerRating(long id, @NonNull Map<Long, Integer> map) {
    this.id = id;
    this.map = map;
  }

  @Override
  public void onRemove() {
    new FreelancerRatingUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().getUnloadUsers();
  }

  @Override
  public @NonNull StarfishFreelancerRating cache() {
    return (StarfishFreelancerRating) FreelancerRating.super.cache();
  }
}
