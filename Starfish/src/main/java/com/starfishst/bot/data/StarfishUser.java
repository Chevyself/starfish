package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.permissions.PermissionStack;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.user.FreelancerRating;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.time.Time;

/** An implementation for {@link BotUser} */
public class StarfishUser implements BotUser {

  @Getter private final long id;
  @NonNull @Getter private final StarfishValuesMap preferences;
  @Getter private final Set<PermissionStack> permissions;

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

  public StarfishUser(long id) {
    this(id, new StarfishValuesMap(), new HashSet<>());
  }

  @Override
  public void setLang(@NonNull String lang) {}

  @Override
  public void onRemove() {
    new BotUserUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().getUnloadUsers();
  }

  @Override
  public FreelancerRating getRating() {
    return Starfish.getLoader().getRating(this.id);
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

  @Override
  public @NonNull StarfishUser cache() {
    return (StarfishUser) BotUser.super.cache();
  }
}
