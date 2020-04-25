package com.starfishst.ethot.commands.provider;

import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.core.context.ICommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.objects.management.AllowedTicketCloserChecker;
import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link com.starfishst.commands.CommandManager} with a method to get a {@link
 * AllowedTicketCloserChecker}
 */
public class AllowedTicketCloserCheckerProvider
    implements IExtraArgumentProvider<AllowedTicketCloserChecker> {

  @Override
  public @NotNull Class<AllowedTicketCloserChecker> getClazz() {
    return AllowedTicketCloserChecker.class;
  }

  @NotNull
  @Override
  public AllowedTicketCloserChecker getObject(@NotNull ICommandContext<?> context)
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
