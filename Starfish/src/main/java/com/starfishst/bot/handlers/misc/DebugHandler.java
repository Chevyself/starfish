package com.starfishst.bot.handlers.misc;

import com.starfishst.api.events.StarfishEvent;
import com.starfishst.bot.handlers.StarfishEventHandler;
import me.googas.commons.events.Listener;
import org.jetbrains.annotations.NotNull;

/** Handler for debugging */
public class DebugHandler implements StarfishEventHandler {

  @Listener(priority = -1)
  public void onStarfishEvent(@NotNull StarfishEvent event) {
    // System.out.println(event);
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "debug";
  }
}
