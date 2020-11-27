package com.starfishst.bot.data;

import com.starfishst.api.PermissionStack;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.api.events.user.BotUserLoadedEvent;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.bot.Starfish;
import java.util.Set;
import me.googas.commons.cache.thread.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link com.starfishst.api.data.user.BotUser} */
public class StarfishUser extends Catchable implements BotUser {

  /** The discord id of the user */
  private final long id;

  /** The preferences of the user */
  @NotNull private final StarfishValuesMap preferences;

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
      long id, @NotNull StarfishValuesMap preferences, Set<PermissionStack> permissions) {
    this.id = id;
    this.preferences = preferences;
    this.permissions = permissions;
    this.addToCache();
    new BotUserLoadedEvent(this).call();
  }

  /**
   * Create an starfish user from a freelancer
   *
   * @param user the user that is supposed to be a freelancer
   * @throws IllegalArgumentException if the user is not a freelancer
   */
  public StarfishUser(@NotNull BotUser user) {
    if (!user.getPreferences().getValueOr("freelancer", Boolean.class, false)) {
      throw new IllegalArgumentException(user + " is not a freelancer!");
    }
    this.id = user.getId();
    this.preferences = new StarfishValuesMap(user.getPreferences().getMap());
    this.permissions = user.getPermissions();
    this.getPreferences().removeValue("freelancer");
    this.getPreferences().removeValue("portfolio");
    new BotUserLoadedEvent(this).call();
    try {
      user.unload(false);
    } catch (Throwable ignored) {
    }
    this.addToCache();
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void setLang(@NotNull String lang) {}

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new BotUserUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Time getToRemove() {
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
  public @NotNull StarfishValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @NotNull LocaleFile getLocaleFile() {
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
