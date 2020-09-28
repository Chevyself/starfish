package com.starfishst.bot.data;

import com.starfishst.api.PermissionStack;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** A class that differentiates users from freelancers */
public class StarfishFreelancer extends StarfishUser {

  /**
   * Create the starfish user
   *
   * @param id the id of the user
   * @param preferences the preferences of the freelancer
   * @param permissions the permission that the freelancer posses
   */
  public StarfishFreelancer(
      long id, @NotNull StarfishValuesMap preferences, @NotNull Set<PermissionStack> permissions) {
    super(id, preferences, permissions);
  }
}
