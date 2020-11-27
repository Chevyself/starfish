package com.starfishst.bot.data;

import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.api.events.user.FreelancerRatingUnloadedEvent;
import com.starfishst.bot.Starfish;
import java.util.Map;
import me.googas.commons.cache.thread.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** Implementation of freelancer rating */
public class StarfishFreelancerRating extends Catchable implements FreelancerRating {

  /** The id of the freelancer that owns this */
  private final long id;

  /** The map that contains the rating of the freelancer */
  @NotNull private final Map<Long, Integer> map;

  /**
   * Create the freelancer rating
   *
   * @param id the id of the freelancer
   * @param map the map that contains the ratings
   */
  public StarfishFreelancerRating(long id, @NotNull Map<Long, Integer> map) {
    this.id = id;
    this.map = map;
    this.addToCache();
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new FreelancerRatingUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Time getToRemove() {
    return Starfish.getConfiguration().toUnloadUser();
  }

  @Override
  public long getId() {
    return this.id;
  }

  @NotNull
  @Override
  public Map<Long, Integer> getMap() {
    return this.map;
  }
}
