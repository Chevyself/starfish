package com.starfishst.api.loader;

import com.starfishst.api.role.BotRole;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.user.FreelancerRating;
import lombok.NonNull;
import me.googas.lazy.Subloader;

public interface UserSubloader extends Subloader {

  /**
   * Get an starfish user by its id
   *
   * @param id the id of the user
   * @return the starfish user
   */
  @NonNull
  BotUser getStarfishUser(long id);

  /**
   * Get a starfish role by its id
   *
   * @param id the id of the role
   * @return the role
   */
  @NonNull
  BotRole getStarfishRole(long id);

  /**
   * Get the rating of a freelancer by its id
   *
   * @param id the id of the freelancer
   * @return the rating of the freelancer
   */
  @NonNull
  FreelancerRating getRating(long id);
}
