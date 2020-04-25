package com.starfishst.ethot.commands.provider;

import com.starfishst.core.ICommandManager;
import com.starfishst.core.context.ICommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IArgumentProvider;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.tickets.TicketManager;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link com.starfishst.commands.CommandManager} with a method to get a {@link Freelancer}
 * when it is send in the message
 */
public class FreelancerProvider implements IArgumentProvider<Freelancer> {

  @Override
  public @NotNull Class<Freelancer> getClazz() {
    return Freelancer.class;
  }

  @NotNull
  @Override
  public Freelancer fromString(@NotNull String s, @NotNull ICommandContext<?> context)
      throws ArgumentProviderException {
    IArgumentProvider<?> provider =
        ICommandManager.getProvider(Member.class, IArgumentProvider.class);
    if (provider != null) {
      Object object = provider.fromString(s, context);
      if (object instanceof Message) {
        Member member = (Member) object;
        Freelancer freelancer =
            TicketManager.getInstance().getLoader().getFreelancer(member.getIdLong());
        if (freelancer != null) {
          return freelancer;
        } else {
          HashMap<String, String> placeholders = new HashMap<>();
          placeholders.put("user", member.getAsMention());
          throw new ArgumentProviderException(Lang.get("NOT_A_FREELANCER", placeholders));
        }
      } else {
        throw new ArgumentProviderException("The provider didn't give a member!");
      }
    } else {
      throw new ArgumentProviderException("The provider for member was not found!");
    }
  }
}
