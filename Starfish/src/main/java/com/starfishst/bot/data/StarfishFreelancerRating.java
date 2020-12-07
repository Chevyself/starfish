package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.api.events.user.FreelancerRatingUnloadedEvent;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.time.Time;

/** Implementation of freelancer rating */
public class StarfishFreelancerRating implements FreelancerRating {

  /** The id of the freelancer that owns this */
  private final long id;

  /** The map that contains the rating of the freelancer */
  @NonNull private final Map<Long, Integer> map;

  /**
   * Create the freelancer rating
   *
   * @param id the id of the freelancer
   * @param map the map that contains the ratings
   */
  public StarfishFreelancerRating(long id, @NonNull Map<Long, Integer> map) {
    this.id = id;
    this.map = map;
    this.cache();
  }

  @Override
  public void onRemove() {
    new FreelancerRatingUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().toUnloadUser();
  }

  @Override
  public long getId() {
    return this.id;
  }

  @NonNull
  @Override
  public Map<Long, Integer> getMap() {
    return this.map;
  }
}
