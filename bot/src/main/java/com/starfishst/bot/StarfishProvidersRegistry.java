package com.starfishst.bot;

import com.starfishst.bot.commands.providers.BotUserProvider;
import com.starfishst.bot.commands.providers.FreelancerProvider;
import com.starfishst.bot.commands.providers.LocaleFileProvider;
import com.starfishst.bot.commands.providers.PermissibleProvider;
import com.starfishst.bot.commands.providers.TicketProvider;
import lombok.NonNull;
import me.googas.commands.jda.messages.MessagesProvider;
import me.googas.commands.jda.providers.registry.JdaProvidersRegistry;

public class StarfishProvidersRegistry extends JdaProvidersRegistry {

  public StarfishProvidersRegistry(@NonNull MessagesProvider messages) {
    super(messages);
    this.addProviders(
        new BotUserProvider(),
        new LocaleFileProvider(),
        new PermissibleProvider(),
        new FreelancerProvider(),
        new TicketProvider());
  }
}
