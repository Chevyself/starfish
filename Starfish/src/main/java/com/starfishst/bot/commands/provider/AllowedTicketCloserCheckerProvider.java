package com.starfishst.bot.commands.provider;

import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.bot.objects.management.AllowedTicketCloserChecker;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link com.starfishst.commands.CommandManager} with a method to get a {@link
 * AllowedTicketCloserChecker}
 */
public class AllowedTicketCloserCheckerProvider
    implements IExtraArgumentProvider<AllowedTicketCloserChecker, CommandContext> {

  @Override
  public @NotNull Class<AllowedTicketCloserChecker> getClazz() {
    return AllowedTicketCloserChecker.class;
  }

  @NotNull
  @Override
  public AllowedTicketCloserChecker getObject(@NotNull CommandContext context)
      throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      AllowedTicketCloserChecker instance = AllowedTicketCloserChecker.getInstance();
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
