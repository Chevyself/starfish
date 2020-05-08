package com.starfishst.bot.commands;

import com.starfishst.bot.config.DiscordConfiguration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.util.Discord;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Optional;
import com.starfishst.commands.annotations.Required;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.Pagination;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

/** All the commands related to the blacklist */
public class BlacklistCommands {

  /**
   * Blacklist an user from creating every other type of tickets besides support and suggestions
   *
   * @param user the user to blacklist
   * @return a successful result if the user has been blacklisted
   * @throws DiscordManipulationException if the guild has not been set while adding the blacklist
   *     roles
   */
  @Command(aliases = "blacklist", description = "Blacklist an user from opening tickets")
  @Parent
  public Result blacklist(
      @Required(name = "user", description = "The user to blacklist") Member user)
      throws DiscordManipulationException {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("user", user.getEffectiveName());
    List<Role> roles = getBlacklistRoles();
    if (Discord.hasRole(user, roles)) {
      return new Result(ResultType.ERROR, Lang.get("USER_ALREADY_BLACKLISTED", placeholders));
    } else {
      Discord.addRoles(user, roles);
      return new Result(Lang.get("USER_BLACKLISTED", placeholders));
    }
  }

  /**
   * Get the list of users inside the blacklist
   *
   * @param page the page to see on the list
   * @return the users inside the blacklist
   * @throws DiscordManipulationException in case that we cannot get the roles
   */
  @Command(
      aliases = "list",
      description = "Get the list of user that are inside the blacklist",
      permission = Permission.ADMINISTRATOR)
  public Result list(
      @Optional(name = "page", description = "The page to see in the list", suggestions = "1")
          int page)
      throws DiscordManipulationException {
    List<Member> members =
        DiscordConfiguration.getInstance().getGuild().getMembersWithRoles(getBlacklistRoles());
    if (!members.isEmpty()) {
      Pagination<Member> pagination = new Pagination<>(members);
      HashMap<String, String> placeholders = new HashMap<>();
      int limit = 10;
      int maxPage = pagination.maxPage(limit);
      placeholders.put("maxPage", String.valueOf(maxPage));
      placeholders.put("page", String.valueOf(page));
      if (page < 1 || page > maxPage) {
        return new Result(ResultType.USAGE, Lang.get("WRONG_PAGE", placeholders));
      } else {
        placeholders.put(
            "list", Lots.pretty(Discord.getAsMention(pagination.getPage(page, limit))));
        return new Result(
            Lang.get("BLACKLIST_LIST", placeholders),
            msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
      }
    } else {
      return new Result(Lang.get("BLACKLIST_EMPTY"));
    }
  }

  /**
   * Removes a user from the blacklist if it is in it
   *
   * @param user the user to remove from the blacklist
   * @return a successful result if the user was removed successfully
   * @throws DiscordManipulationException in case that getting the roles or the guild goes wrong
   */
  @Command(
      aliases = "remove",
      description = "Remove an user from the blacklist",
      permission = Permission.ADMINISTRATOR)
  public Result remove(
      @Required(name = "user", description = "The user to remove from the blacklist") Member user)
      throws DiscordManipulationException {
    List<Role> blacklistRoles = getBlacklistRoles();
    List<Member> list =
        DiscordConfiguration.getInstance().getGuild().getMembersWithRoles(blacklistRoles);
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("user", user.getAsMention());
    if (!list.contains(user)) {
      return new Result(ResultType.ERROR, Lang.get("USER_NOT_BLACKLISTED", placeholders));
    } else {
      Discord.removeRoles(user, blacklistRoles);
      return new Result(
          Lang.get("USER_BLACKLIST_REMOVED", placeholders),
          msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }
  }

  /**
   * Get the roles that are used in the blacklist
   *
   * @return the list of roles used as blacklist
   */
  @NotNull
  private static List<Role> getBlacklistRoles() {
    DiscordConfiguration instance = DiscordConfiguration.getInstance();
    return instance.getRolesByKeys(instance.getRolesKeys("blacklist"));
  }
}
