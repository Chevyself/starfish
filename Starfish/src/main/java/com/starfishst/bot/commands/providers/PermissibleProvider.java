package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.permissions.Permissible;
import com.starfishst.api.user.BotUser;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IArgumentProvider;
import com.starfishst.jda.context.CommandContext;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class PermissibleProvider implements IArgumentProvider<Permissible, CommandContext> {
  @Override
  public @NonNull Class<Permissible> getClazz() {
    return Permissible.class;
  }

  @NonNull
  @Override
  public Permissible fromString(@NonNull String string, @NonNull CommandContext context)
      throws ArgumentProviderException {
    Permissible permissible = null;
    Loader loader = Starfish.getLoader();
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
