package com.starfishst.bot.commands.providers;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides the command context with bot user when is a sender */
public class BotUserSenderProvider implements IExtraArgumentProvider<BotUser, CommandContext> {

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
