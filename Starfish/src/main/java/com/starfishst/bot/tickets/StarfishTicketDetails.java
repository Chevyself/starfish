package com.starfishst.bot.tickets;

import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.ValuesMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Role;

/** An implementation for {@link TicketDetails} */
public class StarfishTicketDetails implements ValuesMap {

  /** The map of preferences */
  @NonNull private final LinkedHashMap<String, Object> preferences;

  /**
   * Create the details
   *
   * @param preferences the map of preferences
   */
  public StarfishTicketDetails(@NonNull LinkedHashMap<String, Object> preferences) {
    this.preferences = preferences;
  }

  @Override
  public @NonNull LinkedHashMap<String, Object> getMap() {
    return this.preferences;
  }

  @NonNull
  @Override
  public Map<String, String> toStringMap() {
    LinkedHashMap<String, String> stringMap = new LinkedHashMap<>();
    this.getMap()
        .forEach(
            (key, value) -> {
              if (value instanceof List) {
                Class<?> clazz = Lots.getClazz((List<?>) value);
                if (clazz != null && Role.class.isAssignableFrom(clazz)) {
                  stringMap.put(key, Lots.pretty(Discord.getRolesAsMention(this.getListValue(key))));
                }
              } else {
                stringMap.put(key, value.toString());
              }
            });
    return stringMap;
  }
}
