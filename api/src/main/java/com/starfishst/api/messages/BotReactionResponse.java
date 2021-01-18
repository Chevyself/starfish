package com.starfishst.api.messages;

import com.starfishst.jda.utils.responsive.ReactionResponse;
import lombok.NonNull;

public interface BotReactionResponse extends ReactionResponse {

  /**
   * Get the type of reaction response
   *
   * @return the type in a string
   */
  @NonNull
  String getType();
}
