package com.starfishst.ethot.commands.provider;

import com.starfishst.core.ICommandManager;
import com.starfishst.core.context.ICommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IArgumentProvider;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link com.starfishst.commands.CommandManager} with a method to get a {@link Freelancer}
 * when it is send in the message
 *
 * @author Chevy
 * @version 1.0.0
 */
public class FreelancerProvider implements IArgumentProvider<Freelancer> {

  @Override
  public @NotNull Class<Freelancer> getClazz() {
    return Freelancer.class;
  }

  @SuppressWarnings("unchecked")
  @NotNull
  @Override
  public Freelancer fromString(@NotNull String s, @NotNull ICommandContext<?> context)
      throws ArgumentProviderException {
    Member member =
        (Member)
            ICommandManager.getProvider(Member.class, IArgumentProvider.class)
                .fromString(s, context);
    Freelancer freelancer = Main.getManager().getLoader().getFreelancer(member.getIdLong());
    if (freelancer != null) {
      return freelancer;
    } else {
      HashMap<String, String> placeholders = new HashMap<>();
      placeholders.put("user", member.getAsMention());
      throw new ArgumentProviderException(Lang.get("NOT_A_FREELANCER", placeholders));
    }
  }
}
