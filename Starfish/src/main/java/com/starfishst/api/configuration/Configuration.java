package com.starfishst.api.configuration;

import com.starfishst.api.Fee;
import com.starfishst.api.ValuesMap;
import com.starfishst.bot.Starfish;
import com.starfishst.jda.ManagerOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** The configuration for {@link Starfish} */
public interface Configuration {

  /**
   * Get the time to unload tickets
   *
   * @return the time to unload tickets from cache
   */
  @NotNull
  Time toUnloadTickets();

  /**
   * Get the time to unload users
   *
   * @return the time to unload users from cache
   */
  @NotNull
  Time toUnloadUser();

  /**
   * Get the time to unload messages
   *
   * @return the time to unload messages from cache
   */
  @NotNull
  Time toUnloadMessages();

  /**
   * Set the total of tickets created
   *
   * @param total the total of tickets created
   */
  void setTotal(long total);

  /**
   * Get the configuration for the mongo loader
   *
   * @return the configuration for mongo
   */
  @NotNull
  MongoConfiguration getMongoConfiguration();

  /**
   * Get the total of tickets created
   *
   * @return the total of tickets created
   */
  long getTotal();

  /**
   * Get the fees applying to a value inside the config
   *
   * @param value the value to get the applying fees
   * @return the collection of fees that apply
   */
  @NotNull
  default Collection<Fee> getFees(double value) {
    List<Fee> fees = new ArrayList<>();
    for (Fee fee : this.getFees()) {
      if (fee.applies(value)) {
        fees.add(fee);
      }
    }
    return fees;
  }

  /**
   * Get the fees inside the config
   *
   * @return the collection of fees
   */
  @NotNull
  Collection<Fee> getFees();

  /**
   * Get the configuration for commands
   *
   * @return the options for commands
   */
  @NotNull
  ManagerOptions getManagerOptions();

  /**
   * Get the preferences for starfish handlers
   *
   * @return the preferences for starfish handlers
   */
  @NotNull
  HashMap<String, ? extends ValuesMap> getHandlerPreferences();
}
