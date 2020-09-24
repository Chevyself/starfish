package com.starfishst.bot.data;

import com.starfishst.api.Preferences;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

public class StarfishPreferences implements Preferences {

  @NotNull private final HashMap<String, Object> preferences;

  public StarfishPreferences(@NotNull HashMap<String, Object> preferences) {
    this.preferences = preferences;
  }

  @Override
  public @NotNull HashMap<String, Object> getPreferences() {
    return this.preferences;
  }
}
