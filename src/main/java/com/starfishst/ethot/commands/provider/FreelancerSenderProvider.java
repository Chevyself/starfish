package com.starfishst.ethot.commands.provider;

import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.core.context.ICommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import org.jetbrains.annotations.NotNull;

public class FreelancerSenderProvider implements IExtraArgumentProvider<Freelancer> {

  @NotNull
  @Override
  public Freelancer getObject(@NotNull ICommandContext<?> context)
      throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      Freelancer freelancer =
          Main.getManager()
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
