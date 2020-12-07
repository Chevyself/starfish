package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.permissions.PermissionStack;
import java.util.Set;
import lombok.NonNull;
import me.googas.commons.time.Time;

/** An implementation for {@link com.starfishst.api.data.user.BotUser} */
public class StarfishUser implements BotUser {

  /** The discord id of the user */
  private final long id;

  /** The preferences of the user */
  @NonNull private final StarfishValuesMap preferences;

  /** The set of permissions that the user has */
  private final Set<PermissionStack> permissions;

  /**
   * Create the starfish user
   *
   * @param id the id of the user
   * @param preferences the preferences of the user
   * @param permissions the set of permissions that the user has
   */
  public StarfishUser(
      long id, @NonNull StarfishValuesMap preferences, Set<PermissionStack> permissions) {
    this.id = id;
    this.preferences = preferences;
    this.permissions = permissions;
  }

  /**
   * Create an starfish user from a freelancer
   *
   * @param user the user that is supposed to be a freelancer
   * @throws IllegalArgumentException if the user is not a freelancer
   */
  public StarfishUser(@NonNull BotUser user) {
    if (!user.getPreferences().getValueOr("freelancer", Boolean.class, false)) {
      throw new IllegalArgumentException(user + " is not a freelancer!");
    }
    this.id = user.getId();
    this.preferences = new StarfishValuesMap(user.getPreferences().getMap());
    this.permissions = user.getPermissions();
    this.getPreferences().removeValue("freelancer");
    this.getPreferences().removeValue("portfolio");
    try {
      user.unload(false);
    } catch (Throwable ignored) {
    }
    this.cache();
  }

  @Override
  public @NonNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void setLang(@NonNull String lang) {}

  @Override
  public void onRemove() {
    new BotUserUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().toUnloadUser();
  }

  @Override
  public FreelancerRating getRating() {
    return Starfish.getLoader().getRating(this.id);
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NonNull StarfishValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @NonNull LocaleFile getLocaleFile() {
    return Starfish.getLanguageHandler().getFile(this.getLang());
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof StarfishUser)) return false;

    StarfishUser that = (StarfishUser) object;

    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
