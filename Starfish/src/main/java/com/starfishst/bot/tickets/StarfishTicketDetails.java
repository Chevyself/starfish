package com.starfishst.bot.tickets;

import com.starfishst.api.data.tickets.TicketDetails;
import java.util.LinkedHashMap;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link TicketDetails} */
public class StarfishTicketDetails implements TicketDetails {

  /** The map of preferences */
  @NotNull private final LinkedHashMap<String, Object> preferences;

  /**
   * Create the details
   *
   * @param preferences the map of preferences
   */
  public StarfishTicketDetails(@NotNull LinkedHashMap<String, Object> preferences) {
    this.preferences = preferences;
  }

  @Override
  public @NotNull LinkedHashMap<String, Object> getPreferences() {
    return this.preferences;
  }
}
