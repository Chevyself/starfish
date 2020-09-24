package com.starfishst.api.data.role;

import com.starfishst.api.Permissible;

/** A role of discord that can be stored in the data of the bot */
public interface BotRole extends Permissible {

  /**
   * Get the id of the role
   *
   * @return the unique id of the role given by discord
   */
  long getId();
}
