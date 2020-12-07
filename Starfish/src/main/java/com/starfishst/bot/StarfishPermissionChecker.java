package com.starfishst.bot;

import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.permissions.JdaPermission;
import com.starfishst.jda.permissions.PermissionChecker;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

/** Checks the permissions for the guido bot */
public class StarfishPermissionChecker implements PermissionChecker {

  /** The messages provider in case that it has to remove a result */
  @NonNull private final MessagesProvider messagesProvider;

  /** The data loader to get the permissions */
  @NonNull private final DataLoader dataLoader;

  /**
   * Create the permission checker
   *
   * @param messagesProvider the messages provider in case it has to return a result
   * @param dataLoader the data loader to get the permissions from the user
   */
  public StarfishPermissionChecker(
      @NonNull MessagesProvider messagesProvider, @NonNull DataLoader dataLoader) {
    this.messagesProvider = messagesProvider;
    this.dataLoader = dataLoader;
  }

  @Override
  public Result checkPermission(@NonNull CommandContext context, @NonNull JdaPermission perm) {
    String permNode = perm.getNode();
    Permission permission = perm.getPermission();
    if (permission != Permission.UNKNOWN) {
      return new Result(ResultType.ERROR, "This operation is not supported by this bot");
    }
    // If a node starts with user: it will check for user permissions not member
    if (!permNode.isEmpty()) {
      if (context instanceof GuildCommandContext && !permNode.startsWith("user:")) {
        BotUser member =
            this.dataLoader.getStarfishUser(
                ((GuildCommandContext) context).getMember().getIdLong());
        if (member.hasPermission(permNode, "discord")
            || ((GuildCommandContext) context)
                .getMember()
                .hasPermission(Permission.ADMINISTRATOR)) {
          return null;
        }
        for (Role role : ((GuildCommandContext) context).getMember().getRoles()) {
          BotRole roleData = this.dataLoader.getStarfishRole(role.getIdLong());
          if (roleData.containsPermission(permNode, "discord")
              && !roleData.hasPermission(permNode, "context")) {
            return new Result(ResultType.PERMISSION, this.messagesProvider.notAllowed(context));
          } else if (roleData.hasPermission(permNode, "discord")) {
            return null;
          }
        }
      } else {
        String node = permNode.startsWith("user:") ? permNode.substring(5) : permNode;
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
  public @NonNull MessagesProvider getMessagesProvider() {
    return this.messagesProvider;
  }
}
