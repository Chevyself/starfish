package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.user.BotUser;
import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;

/** Provides the command manager with bot users */
public class BotUserProvider implements JdaArgumentProvider<BotUser> {

  @Override
  public @NonNull Class<BotUser> getClazz() {
    return BotUser.class;
  }

  @NonNull
  @Override
  public BotUser fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    Object user = context.getRegistry().fromString(s, User.class, context);
    if (user instanceof User) {
      return Starfish.getLoader().getStarfishUser(((User) user).getIdLong());
    } else {
      throw new ArgumentProviderException("Provider did not return an user!");
    }
  }

  @Override
  public boolean provides(@NonNull Class<?> clazz) {
    return clazz == BotUser.class;
  }
}
