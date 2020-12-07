package com.starfishst.api.configuration;

import com.starfishst.api.utility.Fee;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.jda.ManagerOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.time.Time;

/** The configuration for {@link Starfish} */
public interface Configuration {

  /**
   * Get the time to unload tickets
   *
   * @return the time to unload tickets from cache
   */
  @NonNull
  Time toUnloadTickets();

  /**
   * Get the time to unload users
   *
   * @return the time to unload users from cache
   */
  @NonNull
  Time toUnloadUser();

  /**
   * Get the time to unload messages
   *
   * @return the time to unload messages from cache
   */
  @NonNull
  Time toUnloadMessages();

  /**
   * Get the fees applying to a value inside the config
   *
   * @param value the value to get the applying fees
   * @return the collection of fees that apply
   */
  @NonNull
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
   * Get the token to connect with discord
   *
   * @return the token
   */
  @NonNull
  String getToken();

  /**
   * Get the prefix that the bot uses in its commands
   *
   * @return the prefix as a string
   */
  @NonNull
  String getPrefix();

  /**
   * Set the total of tickets created
   *
   * @param total the total of tickets created
   */
  void setTotal(long total);

  /**
   * Get the default lang to which the bot will be running on
   *
   * @return the default lang
   */
  @NonNull
  String getDefaultLang();

  /**
   * Get the total of tickets created
   *
   * @return the total of tickets created
   */
  long getTotal();

  /**
   * Get the configuration for the mongo loader
   *
   * @return the configuration for mongo
   */
  @NonNull
  MongoConfiguration getMongoConfiguration();

  /**
   * Get the fees inside the config
   *
   * @return the collection of fees
   */
  @NonNull
  Collection<Fee> getFees();

  /**
   * Get the configuration for commands
   *
   * @return the options for commands
   */
  @NonNull
  ManagerOptions getManagerOptions();

  /**
   * Get the preferences for starfish handlers
   *
   * @return the preferences for starfish handlers
   */
  @NonNull
  HashMap<String, ? extends ValuesMap> getHandlerPreferences();
}
