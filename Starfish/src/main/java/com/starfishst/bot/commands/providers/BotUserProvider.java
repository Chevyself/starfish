package com.starfishst.bot.commands.providers;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IArgumentProvider;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/** Provides the command manager with bot users */
public class BotUserProvider implements IArgumentProvider<BotUser, CommandContext> {

  @Override
  public @NotNull Class<BotUser> getClazz() {
    return BotUser.class;
  }

  @NotNull
  @Override
  public BotUser fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    Object user = context.getRegistry().fromString(s, User.class, context);
    if (user instanceof User) {
      return Starfish.getLoader().getStarfishUser(context.getSender().getIdLong());
    } else {
      throw new ArgumentProviderException("Provider did not return an user!");
    }
  }
}
