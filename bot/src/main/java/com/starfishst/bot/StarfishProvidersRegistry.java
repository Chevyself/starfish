package com.starfishst.bot;

import com.starfishst.bot.commands.providers.BotUserProvider;
import com.starfishst.bot.commands.providers.BotUserSenderProvider;
import com.starfishst.bot.commands.providers.FreelancerProvider;
import com.starfishst.bot.commands.providers.FreelancerSenderProvider;
import com.starfishst.bot.commands.providers.LocaleFileProvider;
import com.starfishst.bot.commands.providers.PermissibleProvider;
import com.starfishst.bot.commands.providers.TicketProvider;
import com.starfishst.bot.commands.providers.TicketSenderProvider;
import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.messages.MessagesProvider;
import com.starfishst.commands.jda.providers.registry.JdaProvidersRegistry;
import com.starfishst.core.providers.type.IContextualProvider;
import lombok.NonNull;
import me.googas.commons.Lots;

public class StarfishProvidersRegistry extends JdaProvidersRegistry {

  public StarfishProvidersRegistry(@NonNull MessagesProvider messages) {
    super(messages);
    for (IContextualProvider<?, CommandContext> provider :
        Lots.set(
            new BotUserProvider(),
            new BotUserSenderProvider(),
            new LocaleFileProvider(),
            new PermissibleProvider(),
            new FreelancerProvider(),
            new FreelancerSenderProvider(),
            new TicketProvider(),
            new TicketSenderProvider())) {
      this.addProvider(provider);
    }
  }
}
