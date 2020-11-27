package com.starfishst.bot.data;

import com.starfishst.api.PermissionStack;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import java.util.Map;
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
  }

  public static StarfishFreelancer promote(@NotNull BotUser user) {
    try {
      user.unload(false);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    return new StarfishFreelancer(user);
  }

  @Override
  public @NotNull Map<String, String> getPlaceholders() {
    Map<String, String> placeholders = super.getPlaceholders();
    placeholders.put(
        "rating", this.getRating().getReadable(Starfish.getLanguageHandler().getDefault()));
    return placeholders;
  }
}
