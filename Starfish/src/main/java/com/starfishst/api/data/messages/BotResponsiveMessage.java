package com.starfishst.api.data.messages;

import com.starfishst.api.Preferences;
import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import com.starfishst.core.utils.cache.ICatchable;

/** An implementation for {@link ResponsiveMessage} */
public interface BotResponsiveMessage extends ResponsiveMessage, ICatchable {

  /** Deletes the responsive message */
  void delete();

  /**
   * Get the data of the responsive message
   *
   * @return the data
   */
  Preferences getData();
}
