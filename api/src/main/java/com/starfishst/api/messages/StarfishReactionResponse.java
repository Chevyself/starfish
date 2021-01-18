package com.starfishst.api.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.messages.BotReactionResponse;
import com.starfishst.api.messages.BotResponsiveMessage;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.annotations.Nullable;

public abstract class StarfishReactionResponse implements BotReactionResponse {

  @Nullable @Getter @Setter protected transient BotResponsiveMessage message;

  protected StarfishReactionResponse(@Nullable BotResponsiveMessage message) {
    this.message = message;
  }

  @NonNull
  public static String getUnicode(@NonNull String key) {
    return Starfish.getLanguageHandler().getDefault().get(key);
  }
}
