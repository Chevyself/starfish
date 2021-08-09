package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.user.BotUser;
import com.starfishst.bot.commands.objects.Freelancer;
import lombok.NonNull;
import me.googas.commands.exceptions.ArgumentProviderException;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.providers.type.JdaArgumentProvider;
import me.googas.commands.jda.providers.type.JdaExtraArgumentProvider;
import net.dv8tion.jda.api.entities.User;

/** Provides command manager with freelancers */
public class FreelancerProvider
    implements JdaArgumentProvider<Freelancer>, JdaExtraArgumentProvider<Freelancer> {

  @Override
  public @NonNull Class<Freelancer> getClazz() {
    return Freelancer.class;
  }

  @NonNull
  @Override
  public Freelancer fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    Object user = context.getRegistry().fromString(s, User.class, context);
    Loader loader = Starfish.getLoader();
    BotUser sender = loader.getStarfishUser(context.getSender().getIdLong());
    if (user instanceof User) {
      BotUser botUser = loader.getStarfishUser(((User) user).getIdLong());
      if (botUser.isFreelancer()) {
        return new Freelancer(botUser);
      }
      throw new ArgumentProviderException(
          sender.getLocaleFile().get("user.is-not-freelancer", botUser.getPlaceholders()));
    } else {
      throw new ArgumentProviderException("Provider did not return an user!");
    }
  }

  @NonNull
  @Override
  public Freelancer getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    Loader loader = Starfish.getLoader();
    BotUser botUser = loader.getStarfishUser(context.getSender().getIdLong());
    if (botUser.isFreelancer()) {
      return new Freelancer(botUser);
    }
    throw new ArgumentProviderException(botUser.getLocaleFile().get("user.not-freelancer"));
  }
}
