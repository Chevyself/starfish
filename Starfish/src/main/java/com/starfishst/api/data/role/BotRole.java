package com.starfishst.api.data.role;

import com.starfishst.api.permissions.Permissible;
import com.starfishst.api.utility.StarfishCatchable;

/** A role of discord that can be stored in the data of the bot */
public interface BotRole extends Permissible, StarfishCatchable {

  /**
   * Get the id of the role
   *
   * @return the unique id of the role given by discord
   */
  long getId();
}
