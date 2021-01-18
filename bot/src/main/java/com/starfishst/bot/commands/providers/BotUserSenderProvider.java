package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.user.BotUser;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;

/** Provides the command context with bot user when is a sender */
public class BotUserSenderProvider implements JdaExtraArgumentProvider<BotUser> {

  @Override
  public @NonNull Class<BotUser> getClazz() {
    return BotUser.class;
  }

  @NonNull
  @Override
  public BotUser getObject(@NonNull CommandContext context) {
    return Starfish.getLoader().getStarfishUser(context.getSender().getIdLong());
  }
}
