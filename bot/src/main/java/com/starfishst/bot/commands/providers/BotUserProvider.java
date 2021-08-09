package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.user.BotUser;
import lombok.NonNull;
import me.googas.commands.exceptions.ArgumentProviderException;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.providers.type.JdaArgumentProvider;
import me.googas.commands.jda.providers.type.JdaExtraArgumentProvider;
import net.dv8tion.jda.api.entities.User;

/** Provides the command manager with bot users */
public class BotUserProvider
    implements JdaArgumentProvider<BotUser>, JdaExtraArgumentProvider<BotUser> {

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

  @NonNull
  @Override
  public BotUser getObject(@NonNull CommandContext context) {
    return Starfish.getLoader().getStarfishUser(context.getSender().getIdLong());
  }
}
