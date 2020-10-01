package com.starfishst.api.data.messages;

import com.starfishst.api.ValuesMap;
import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import com.starfishst.core.utils.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link ResponsiveMessage} */
public interface BotResponsiveMessage extends ResponsiveMessage, ICatchable {

  /** Deletes the responsive message */
  void delete();

  /**
   * Get the data of the responsive message
   *
   * @return the data
   */
  @NotNull
  ValuesMap getData();

  /**
   * Get the type of the message
   *
   * @return the type of the message
   */
  @NotNull
  String getType();
}
