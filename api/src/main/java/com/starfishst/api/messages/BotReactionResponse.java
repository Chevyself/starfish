package com.starfishst.api.messages;

import lombok.NonNull;
import me.googas.commands.jda.utils.responsive.ReactionResponse;

public interface BotReactionResponse extends ReactionResponse {

  /**
   * Get the type of reaction response
   *
   * @return the type in a string
   */
  @NonNull
  String getType();
}
