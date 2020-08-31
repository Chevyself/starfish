package com.starfishst.bot.commands.provider;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.objects.management.AllowedTicketManagerChecker;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link com.starfishst.commands.CommandManager} with a method to get a {@link
 * AllowedTicketManagerChecker}
 */
public class AllowedTicketManagerCheckerProvider
    implements IExtraArgumentProvider<AllowedTicketManagerChecker, CommandContext> {

  @Override
  public @NotNull Class<AllowedTicketManagerChecker> getClazz() {
    return AllowedTicketManagerChecker.class;
  }

  @NotNull
  @Override
  public AllowedTicketManagerChecker getObject(@NotNull CommandContext context)
      throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      AllowedTicketManagerChecker instance = AllowedTicketManagerChecker.getInstance();
      if (instance.isAllowed(((GuildCommandContext) context).getMember())) {
        return instance;
      } else {
        throw new ArgumentProviderException(Lang.get("NO_PERMISSION"));
      }
    } else {
      throw new ArgumentProviderException(Lang.get("GUILD_ONLY"));
    }
  }
}
