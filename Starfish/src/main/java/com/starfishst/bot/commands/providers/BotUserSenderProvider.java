package com.starfishst.bot.commands.providers;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides the command context with bot user when is a sender */
public class BotUserSenderProvider implements JdaExtraArgumentProvider<BotUser> {

  @Override
  public @NotNull Class<BotUser> getClazz() {
    return BotUser.class;
  }

  @NotNull
  @Override
  public BotUser getObject(@NotNull CommandContext context) {
    return Starfish.getLoader().getStarfishUser(context.getSender().getIdLong());
  }
}
