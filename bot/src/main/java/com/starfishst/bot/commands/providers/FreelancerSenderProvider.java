package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.user.BotUser;
import com.starfishst.bot.commands.objects.Freelancer;
import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;

/** Provides freelancers as a sender */
public class FreelancerSenderProvider implements JdaExtraArgumentProvider<Freelancer> {
  @Override
  public @NonNull Class<Freelancer> getClazz() {
    return Freelancer.class;
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
