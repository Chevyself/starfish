package com.starfishst.api.data.messages;

import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import lombok.NonNull;

/** An implementation for {@link ResponsiveMessage} */
public interface BotResponsiveMessage extends ResponsiveMessage, StarfishCatchable {

  /** Deletes the responsive message */
  void delete();

  /**
   * Get the data of the responsive message
   *
   * @return the data
   */
  @NonNull
  ValuesMap getData();

  /**
   * Get the type of the message
   *
   * @return the type of the message
   */
  @NonNull
  String getType();
}
