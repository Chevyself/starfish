package com.starfishst.api.data.user;

import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.StarfishCatchable;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.Maps;

/** This object represents a map that contains rating given to a freelancer */
public interface FreelancerRating extends StarfishCatchable {

  /**
   * Adds a rating
   *
   * @param userIdLong the user that added the reaction
   * @param value the rating that the user gave
   */
  default void addRating(long userIdLong, int value) {
    this.getMap().put(userIdLong, value);
  }

  /**
   * Get the readable rating
   *
   * @param locale the locale which will read the rating
   * @return the readable rating
   */
  @NonNull
  default String getReadable(@NonNull LocaleFile locale) {
    if (this.getMap().isEmpty()) {
      return locale.get("freelancer-rating.empty");
    } else {
      return locale.get(
          "freelancer-rating.rating",
          Maps.builder("average", String.valueOf(this.getRating()))
              .append("total", String.valueOf(this.getMap().size())));
    }
  }

  /**
   * Get the average rating
   *
   * @return the average rating
   */
  default int getRating() {
    if (this.getMap().isEmpty()) {
      return 0;
    } else {
      int sum = 0;
      for (Integer value : this.getMap().values()) {
        if (value != null) sum += value;
      }
      return sum / this.getMap().size();
    }
  }

  /**
   * Get the id of the freelancer that was rated
   *
   * @return the id of the freelancer as a long. This is the id of the freelancer in discord
   */
  long getId();

  /**
   * This is the map of ratings of this freelancer. The key is the id of the user that rated the
   * freelancer and the integer is the given rating, it must be a number from 0 to 5
   *
   * @return the map of ratings of this freelancer.
   */
  Map<Long, Integer> getMap();
}
