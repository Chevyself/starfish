package com.starfishst.bot.data;

import com.starfishst.api.PermissionStack;
import com.starfishst.api.data.user.BotUser;
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

  /**
   * Create the freelancer
   *
   * @param user the user that has been promoted to freelancer
   */
  public StarfishFreelancer(@NotNull BotUser user) {
    this(
        user.getId(), new StarfishValuesMap(user.getPreferences().getMap()), user.getPermissions());
    this.getPreferences().addValue("freelancer", true);
    user.unload(false);
  }
}
