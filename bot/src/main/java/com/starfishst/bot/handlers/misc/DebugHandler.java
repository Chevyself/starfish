package com.starfishst.bot.handlers.misc;

import com.starfishst.api.events.StarfishEvent;
import com.starfishst.api.events.StarfishHandler;
import lombok.NonNull;
import me.googas.commons.events.Listener;

/** Handler for debugging */
public class DebugHandler implements StarfishHandler {

  @Listener(priority = -1)
  public void onStarfishEvent(@NonNull StarfishEvent event) {
    // System.out.println(event);
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "debug";
  }
}
