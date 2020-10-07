package com.starfishst.bot;

import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.jda.PermissionChecker;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Checks the permissions for the guido bot */
public class StarfishPermissionChecker implements PermissionChecker {

  /** The messages provider in case that it has to remove a result */
  @NotNull private final MessagesProvider messagesProvider;

  /** The data loader to get the permissions */
  @NotNull private final DataLoader dataLoader;

  /**
   * Create the permission checker
   *
   * @param messagesProvider the messages provider in case it has to return a result
   * @param dataLoader the data loader to get the permissions from the user
   */
  public StarfishPermissionChecker(
      @NotNull MessagesProvider messagesProvider, @NotNull DataLoader dataLoader) {
    this.messagesProvider = messagesProvider;
    this.dataLoader = dataLoader;
  }

  @Override
  public @Nullable Result checkPermission(@NotNull CommandContext context, @NotNull Perm perm) {
    if (perm.permission() != Permission.UNKNOWN) {
      return new Result(ResultType.ERROR, "This operation is not supported by this bot");
    }
    // If a node starts with user: it will check for user permissions not member
    if (!perm.node().isEmpty()) {
      if (context instanceof GuildCommandContext && !perm.node().startsWith("user:")) {
        BotUser member =
            this.dataLoader.getStarfishUser(
                ((GuildCommandContext) context).getMember().getIdLong());
        if (member.hasPermission(perm.node(), "discord")
            || ((GuildCommandContext) context)
                .getMember()
                .hasPermission(Permission.ADMINISTRATOR)) {
          return null;
        }
        for (Role role : ((GuildCommandContext) context).getMember().getRoles()) {
          BotRole roleData = this.dataLoader.getStarfishRole(role.getIdLong());
          if (roleData.containsPermission(perm.node(), "discord")
              && !roleData.hasPermission(perm.node(), "context")) {
            return new Result(ResultType.PERMISSION, this.messagesProvider.notAllowed(context));
          } else if (roleData.hasPermission(perm.node(), "discord")) {
            return null;
          }
        }
      } else {
        String node = perm.node().startsWith("user:") ? perm.node().substring(5) : perm.node();
        BotUser userData = this.dataLoader.getStarfishUser(context.getSender().getIdLong());
        if (userData.hasPermission(node, "discord:user")) {
          return null;
        }
      }
      return new Result(ResultType.PERMISSION, this.messagesProvider.notAllowed(context));
    }
    return null;
  }

  @Override
  public @NotNull MessagesProvider getMessagesProvider() {
    return this.messagesProvider;
  }
}
