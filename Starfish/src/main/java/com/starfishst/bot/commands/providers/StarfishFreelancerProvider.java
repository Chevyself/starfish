package com.starfishst.bot.commands.providers;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.data.StarfishFreelancer;
import com.starfishst.bot.tickets.StarfishLoader;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/** Provides command manager with freelancers */
public class StarfishFreelancerProvider implements JdaArgumentProvider<StarfishFreelancer> {

  @Override
  public @NotNull Class<StarfishFreelancer> getClazz() {
    return StarfishFreelancer.class;
  }

  @NotNull
  @Override
  public StarfishFreelancer fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    Object user = context.getRegistry().fromString(s, User.class, context);
    StarfishLoader loader = Starfish.getLoader();
    BotUser sender = loader.getStarfishUser(context.getSender().getIdLong());
    if (user instanceof User) {
      BotUser botUser = loader.getStarfishUser(((User) user).getIdLong());
      if (botUser instanceof StarfishFreelancer) {
        return (StarfishFreelancer) botUser;
      } else {
        throw new ArgumentProviderException(
            sender.getLocaleFile().get("user.is-not-freelancer", botUser.getPlaceholders()));
      }
    } else {
      throw new ArgumentProviderException("Provider did not return an user!");
    }
  }

  @Override
  public boolean provides(@NotNull Class<?> clazz) {
    return clazz == StarfishFreelancer.class;
  }
}
