package com.starfishst.bot;

import com.starfishst.api.loader.Loader;
import com.starfishst.api.role.BotRole;
import com.starfishst.api.user.BotUser;
import lombok.NonNull;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.context.GuildCommandContext;
import me.googas.commands.jda.messages.MessagesProvider;
import me.googas.commands.jda.permissions.EasyPermission;
import me.googas.commands.jda.permissions.PermissionChecker;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

/** Checks the permissions for the guido bot */
public class StarfishPermissionChecker implements PermissionChecker {

  /** The messages provider in case that it has to remove a result */
  @NonNull private final MessagesProvider messagesProvider;

  /** The data loader to get the permissions */
  @NonNull private final Loader dataLoader;

  /**
   * Create the permission checker
   *
   * @param messagesProvider the messages provider in case it has to return a result
   * @param dataLoader the data loader to get the permissions from the user
   */
  public StarfishPermissionChecker(
      @NonNull MessagesProvider messagesProvider, @NonNull Loader dataLoader) {
    this.messagesProvider = messagesProvider;
    this.dataLoader = dataLoader;
  }

  @Override
  public Result checkPermission(@NonNull CommandContext context, @NonNull EasyPermission perm) {
    String permNode = perm.getNode();
    Permission permission = perm.getPermission();
    if (permission != Permission.UNKNOWN) {
      return Result.forType(ResultType.ERROR)
          .setDescription("This operation is not support by this bot")
          .build();
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
            return Result.forType(ResultType.PERMISSION)
                .setDescription(this.messagesProvider.notAllowed(context))
                .build();
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
      return Result.forType(ResultType.PERMISSION)
          .setDescription(this.messagesProvider.notAllowed(context))
          .build();
    }
    return null;
  }

  @Override
  public @NonNull MessagesProvider getMessagesProvider() {
    return this.messagesProvider;
  }
}
