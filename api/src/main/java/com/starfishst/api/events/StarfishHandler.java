package com.starfishst.api.events;

import com.starfishst.api.Starfish;
import com.starfishst.api.utility.ValuesMap;
import java.util.HashMap;
import lombok.NonNull;
import me.googas.starbox.events.ListenerManager;
import net.dv8tion.jda.api.JDA;

/** Handles JDA events */
public interface StarfishHandler {

  /**
   * Register the handler
   *
   * @param api the api to register the handler to
   */
  default void register(@NonNull JDA api, @NonNull ListenerManager listenerManager) {
    api.addEventListener(this);
    listenerManager.parseAndRegister(this);
  }

  /**
   * Unregister the handler
   *
   * @param api the api to unregister the handler from
   */
  default void unregister(@NonNull JDA api) {
    api.removeEventListener(this);
    Starfish.getListenerManager().unregister(this);
  }

  /** Called on {@link #unregister(JDA)} */
  default void onUnload() {}

  /** Called when the bot is ready to use */
  default void onEnable() {}

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
  @NonNull
  default ValuesMap getPreferences() {
    ValuesMap valuesMap = Starfish.getConfiguration().getHandlerPreferences().get(this.getName());
    if (valuesMap != null) {
      return valuesMap;
    } else {
      return new ValuesMap(new HashMap<>());
    }
  }
}
