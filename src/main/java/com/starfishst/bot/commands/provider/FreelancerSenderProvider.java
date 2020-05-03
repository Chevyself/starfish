package com.starfishst.bot.commands.provider;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link com.starfishst.commands.CommandManager} with a method to get a {@link Freelancer}
 * when it is a sender
 */
public class FreelancerSenderProvider
    implements IExtraArgumentProvider<Freelancer, CommandContext> {

  @NotNull
  @Override
  public Freelancer getObject(@NotNull CommandContext context) throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      Freelancer freelancer =
          TicketManager.getInstance()
              .getLoader()
              .getFreelancer(((GuildCommandContext) context).getMember().getIdLong());
      if (freelancer != null) {
        return freelancer;
      } else {
        throw new ArgumentProviderException(Lang.get("NOT_FREELANCER"));
      }
    }
    throw new IllegalArgumentException(Lang.get("GUILD_ONLY"));
  }

  @Override
  public @NotNull Class<Freelancer> getClazz() {
    return Freelancer.class;
  }
}
