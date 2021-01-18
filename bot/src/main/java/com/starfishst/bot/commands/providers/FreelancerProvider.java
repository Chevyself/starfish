package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.user.BotUser;
import com.starfishst.bot.commands.objects.Freelancer;
import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;

/** Provides command manager with freelancers */
public class FreelancerProvider implements JdaArgumentProvider<Freelancer> {

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
}
