package com.starfishst.bot.commands.providers;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.data.StarfishFreelancer;
import com.starfishst.bot.tickets.StarfishLoader;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides freelancers as a sender */
public class StarfishFreelancerSenderProvider
    implements IExtraArgumentProvider<StarfishFreelancer, CommandContext> {
  @Override
  public @NotNull Class<StarfishFreelancer> getClazz() {
    return StarfishFreelancer.class;
  }

  @NotNull
  @Override
  public StarfishFreelancer getObject(@NotNull CommandContext context)
      throws ArgumentProviderException {
    StarfishLoader loader = Starfish.getLoader();
    BotUser botUser = loader.getStarfishUser(context.getSender().getIdLong());
    if (botUser instanceof StarfishFreelancer) {
      return (StarfishFreelancer) botUser;
    } else {
      throw new ArgumentProviderException(botUser.getLocaleFile().get("user.not-freelancer"));
    }
  }

  @Override
  public boolean provides(@NotNull Class<?> clazz) {
    return clazz == StarfishFreelancer.class;
  }
}
