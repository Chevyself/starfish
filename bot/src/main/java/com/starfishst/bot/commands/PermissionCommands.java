package com.starfishst.bot.commands;

import com.starfishst.api.permissions.Permissible;
import com.starfishst.api.permissions.Permission;
import com.starfishst.api.permissions.PermissionStack;
import com.starfishst.bot.data.StarfishPermission;
import com.starfishst.bot.tickets.StarfishPermissionStack;
import java.util.HashSet;
import me.googas.commands.annotations.Parent;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;

/** Commands related to permissions */
public class PermissionCommands {

  /**
   * See the permissions from a permissible entity
   *
   * @param permissible the permissible entity to see the permissions from
   * @return the permissions
   */
  @Parent
  @Command(
      aliases = {"permissions", "perms"},
      description = "permissions.desc",
      node = "starfish.permissions")
  public Result permissions(
      @Required(name = "permissions.permissible", description = "permissions.permissible.desc")
          Permissible permissible) {
    StringBuilder builder = new StringBuilder();
    for (PermissionStack stack : permissible.getPermissions()) {
      builder.append("**Context: ").append(stack.getContext()).append("**").append("\n");
      for (Permission permission : stack.getPermissions()) {
        builder.append("  - ").append(permission.getNodeAppended()).append("\n");
      }
    }
    return new Result(builder.toString());
  }

  /**
   * Give a permission to a permissible
   *
   * @param permissible the permissible to give the permission
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   * @return the result of the command
   */
  @Command(
      aliases = {"add", "give"},
      description = "Add a permission to an permissible")
  public Result permission(
      @Required(name = "Permissible", description = "The entity to give the permission to")
          Permissible permissible,
      @Required(name = "node", description = "The node of the permission") String node,
      @Required(name = "enabled", description = "Whether the permission is enabled")
          boolean enabled) {
    permissible
        .getPermissions("discord")
        .orElseGet(
            () -> {
              PermissionStack stack = new StarfishPermissionStack("discord", new HashSet<>());
              permissible.getPermissions().add(stack);
              return stack;
            })
        .getPermissions()
        .add(new StarfishPermission(node, enabled));
    return new Result(
        "The permission " + node + " in status " + enabled + " has been given to " + permissible);
  }

  /**
   * Remove a permission from a permissible
   *
   * @param permissible the permissible to remove the permission
   * @param node the node of the permission
   * @return the result of the command
   */
  @Command(
      aliases = {"remove", "revoke"},
      description = "Add a permission to an permissible")
  public Result remove(
      @Required(name = "Permissible", description = "The entity to give the permission to")
          Permissible permissible,
      @Required(name = "node", description = "The node of the permission") String node) {
    permissible
        .getPermissions("discord")
        .ifPresent(
            discord -> {
              discord
                  .getPermissions()
                  .removeIf(permission -> permission.getNode().equalsIgnoreCase(node));
            });
    return new Result("The permission " + node + " has been removed from " + permissible);
  }
}
