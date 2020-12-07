package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.commands.objects.Freelancer;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
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
    DataLoader loader = Starfish.getLoader();
    BotUser botUser = loader.getStarfishUser(context.getSender().getIdLong());
    if (botUser.isFreelancer()) {
      return new Freelancer(botUser);
    }
    throw new ArgumentProviderException(botUser.getLocaleFile().get("user.not-freelancer"));
  }
}
