package com.starfishst.ethot.commands.provider;

import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.core.context.ICommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.objects.management.AllowedTicketManagerChecker;
import org.jetbrains.annotations.NotNull;

/**
 * Provides {@link com.starfishst.commands.CommandManager} with a method to get a {@link
 * AllowedTicketManagerChecker}
 */
public class AllowedTicketManagerCheckerProvider
    implements IExtraArgumentProvider<AllowedTicketManagerChecker> {

  @Override
  public @NotNull Class<AllowedTicketManagerChecker> getClazz() {
    return AllowedTicketManagerChecker.class;
  }

  @NotNull
  @Override
  public AllowedTicketManagerChecker getObject(@NotNull ICommandContext<?> context)
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
