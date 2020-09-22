package com.starfishst.bot.commands.provider;

import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.oldtickets.TicketManager;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.providers.registry.ImplProvidersRegistry;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IArgumentProvider;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link com.starfishst.commands.CommandManager} with a method to get a {@link Freelancer}
 * when it is send in the message
 */
public class FreelancerProvider implements IArgumentProvider<Freelancer, CommandContext> {

  @Override
  public @NotNull Class<Freelancer> getClazz() {
    return Freelancer.class;
  }

  @NotNull
  @Override
  public Freelancer fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    Object object = ImplProvidersRegistry.getInstance().fromString(s, Member.class, context);
    if (object instanceof Member) {
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
  }
}
