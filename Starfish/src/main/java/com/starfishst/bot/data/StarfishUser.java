package com.starfishst.bot.data;

import com.starfishst.api.PermissionStack;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.user.BotUserLoadedEvent;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.bot.Starfish;
import com.starfishst.core.utils.cache.Catchable;
import java.util.Set;
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
    super(Starfish.getConfiguration().toUnloadUser());
    this.id = id;
    this.preferences = preferences;
    this.permissions = permissions;
    new BotUserLoadedEvent(this).call();
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void setLang(@NotNull String lang) {}

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {
    new BotUserUnloadedEvent(this).call();
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
}
