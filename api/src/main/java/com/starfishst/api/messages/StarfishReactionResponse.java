package com.starfishst.api.messages;

import com.starfishst.api.Starfish;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public abstract class StarfishReactionResponse implements BotReactionResponse {

  @Getter @Setter protected transient BotResponsiveMessage message;

  protected StarfishReactionResponse(BotResponsiveMessage message) {
    this.message = message;
  }

  @NonNull
  public static String getUnicode(@NonNull String key) {
    return Starfish.getLanguageHandler().getDefault().get(key);
  }
}
