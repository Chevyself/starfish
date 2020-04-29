package com.starfishst.ethot.config;

import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.exception.PunishmentsInitException;
import com.starfishst.simple.config.JsonConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/** A configuration related to punishment. As of now only mutes */
public class PunishmentsConfiguration extends JsonConfiguration {

  /** The instance for static usage */
  @NotNull private static PunishmentsConfiguration instance;

  static {
    try {
      instance = JsonConfiguration.getInstance("punishments.json", PunishmentsConfiguration.class);
    } catch (IOException e) {
      Errors.addError(e.getMessage());
      throw new PunishmentsInitException();
    }
  }

  /** The list of muted users */
  @NotNull private final List<User> mutes;

  /** The creator for json */
  public PunishmentsConfiguration() {
    this.mutes = new ArrayList<>();
  }

  /**
   * Checks if a user is muted
   *
   * @param user the user to check
   * @return true if muted
   */
  public boolean isMuted(@NotNull User user) {
    return mutes.contains(user);
  }

  /**
   * Mutes a user
   *
   * @param user the user to mute
   */
  public void mute(@NotNull User user) {
    mutes.add(user);
  }

  /**
   * Unmute a user
   *
   * @param user the user to unmute
   */
  public void unmute(@NotNull User user) {
    mutes.remove(user);
  }

  /**
   * Get the static instance
   *
   * @return the static instance
   */
  @NotNull
  public static PunishmentsConfiguration getInstance() {
    return instance;
  }
}
