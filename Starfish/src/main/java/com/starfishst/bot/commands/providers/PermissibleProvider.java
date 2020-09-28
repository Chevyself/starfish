package com.starfishst.bot.commands.providers;

import com.starfishst.api.Permissible;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.tickets.StarfishLoader;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IArgumentProvider;
import com.starfishst.core.utils.maps.Maps;
import java.util.List;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class PermissibleProvider implements IArgumentProvider<Permissible, CommandContext> {
  @Override
  public @NotNull Class<Permissible> getClazz() {
    return Permissible.class;
  }

  @NotNull
  @Override
  public Permissible fromString(@NotNull String string, @NotNull CommandContext context)
      throws ArgumentProviderException {
    Permissible permissible = null;
    StarfishLoader loader = Starfish.getLoader();
    BotUser sender = loader.getStarfishUser(context.getSender().getIdLong());
    List<User> mentionedUsers = context.getMessage().getMentionedUsers();
    for (User mentionedUser : mentionedUsers) {
      if (string.contains(mentionedUser.getId())) {
        permissible = loader.getStarfishUser(mentionedUser.getIdLong());
        break;
      }
    }
    List<Role> mentionedRoles = context.getMessage().getMentionedRoles();
    for (Role mentionedRole : mentionedRoles) {
      if (string.contains(mentionedRole.getId())) {
        permissible = loader.getStarfishRole(mentionedRole.getIdLong());
        break;
      }
    }
    if (permissible != null) {
      return permissible;
    } else {
      throw new ArgumentProviderException(
          sender.getLocaleFile().get("invalid.permissible", Maps.singleton("string", string)));
    }
  }
}
