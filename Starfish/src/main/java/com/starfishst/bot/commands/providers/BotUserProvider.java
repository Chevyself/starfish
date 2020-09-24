package com.starfishst.bot.commands.providers;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.core.providers.type.IArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides the command manager with bot users */
public class BotUserProvider implements IArgumentProvider<BotUser, CommandContext> {

  @Override
  public @NotNull Class<BotUser> getClazz() {
    return BotUser.class;
  }

  @NotNull
  @Override
  public BotUser fromString(@NotNull String s, @NotNull CommandContext context) {
    return Starfish.getLoader().getStarfishUser(context.getSender().getIdLong());
  }
}
