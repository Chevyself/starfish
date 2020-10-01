package com.starfishst.bot.tickets;

import com.starfishst.api.data.tickets.TicketDetails;
import com.starfishst.api.utility.Discord;
import com.starfishst.core.utils.Lots;
import java.util.LinkedHashMap;
import java.util.Map;
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
  public @NotNull LinkedHashMap<String, Object> getMap() {
    return this.preferences;
  }

  @NotNull
  @Override
  public Map<String, String> toStringMap() {
    LinkedHashMap<String, String> stringMap = new LinkedHashMap<>();
    this.getMap()
        .forEach(
            (key, value) -> {
              if (key.startsWith("role")) {
                stringMap.put(key, Lots.pretty(Discord.getAsMention(this.getLisValue(key))));
              } else {
                stringMap.put(key, value.toString());
              }
            });
    return stringMap;
  }
}
