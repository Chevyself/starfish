package com.starfishst.bot.handlers;

import com.starfishst.api.ValuesMap;
import com.starfishst.bot.Starfish;
import java.util.HashMap;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/** Handles JDA events */
public interface StarfishHandler {

  /**
   * Register the handler
   *
   * @param api the api to register the handler to
   */
  default void register(@NotNull JDA api) {
    api.addEventListener(this);
  }

  /**
   * Unregister the handler
   *
   * @param api the api to unregister the handler from
   */
  default void unregister(@NotNull JDA api) {
    this.onUnload();
    api.removeEventListener(this);
  }

  /** Called on {@link #unregister(JDA)} */
  void onUnload();

  /**
   * Get the name of this handler
   *
   * @return the name of the handler
   */
  String getName();

  /**
   * Get the preferences for the handler
   *
   * @return the preferences
   */
  @NotNull
  default ValuesMap getPreferences() {
    ValuesMap valuesMap = Starfish.getConfiguration().getHandlerPreferences().get(this.getName());
    if (valuesMap != null) {
      return valuesMap;
    } else {
      return new StarfishHandlerValuesMap(new HashMap<>());
    }
  }
}
