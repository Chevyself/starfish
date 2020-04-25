package com.starfishst.ethot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Optional;
import com.starfishst.commands.annotations.Required;
import com.starfishst.commands.result.Result;
import com.starfishst.core.arguments.JoinedStrings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/** Commands related to moderation */
public class ModerationCommands {

  /**
   * Mutes an specified user
   *
   * @param user the user to mute
   * @param reason the reason to mute the user
   * @return a successful result if the user was muted
   */
  @Command(
      aliases = "mute",
      description = "Mute the specified user",
      permission = Permission.ADMINISTRATOR)
  public Result mute(
      @Required(name = "user", description = "The user to mute") Member user,
      @Optional(
              name = "reason",
              description = "The reason to mute the user",
              suggestions = "No reason")
          JoinedStrings reason) {
    // TODO
    return new Result();
  }

  /**
   * Unmute an specified user
   *
   * @param user the user to unmute
   * @return a successful result if the user was unmute
   */
  @Command(
      aliases = "unmute",
      description = "Unmute the specified user",
      permission = Permission.ADMINISTRATOR)
  public Result unmute(@Required(name = "user", description = "The user to unmute") Member user) {
    // TODO
    return new Result();
  }

  /**
   * Ban a specified user
   *
   * @param user the user to ban
   * @param reason the reason to ban it
   * @return a successful result if the user was banned
   */
  @Command(
      aliases = "ban",
      description = "Ban the specified user",
      permission = Permission.ADMINISTRATOR)
  public Result ban(
      @Required(name = "user", description = "The user to ban") Member user,
      @Required(name = "reason", description = "The reason of the ban") JoinedStrings reason) {
    // TODO
    return new Result();
  }

  /**
   * Unbans an specified user
   *
   * @param user the user to unban
   * @return a successful result if the user was unbanned
   */
  @Command(
      aliases = {"unban", "pardon"},
      description = "Unban the specified user",
      permission = Permission.ADMINISTRATOR)
  public Result unban(@Required(name = "user", description = "The user to unban") User user) {
    // TODO
    return new Result();
  }

  /**
   * Clears the specified amount of messages
   *
   * @param channel the channel to delete messages from
   * @param amount the amount to clear
   * @return a successful result if the messages were deleted
   */
  @Command(aliases = "clear", description = "Clear the specified amount of messages")
  public Result clear(
      TextChannel channel,
      @Required(name = "amount", description = "The amount of lines to clear") int amount) {
    // TODO
    // Do not clear ticket channels
    return new Result();
  }

  /**
   * Blacklist an user from creating every other type of tickets besides support and suggestions
   *
   * @param user the user to blacklist
   * @return a successful result if the user has been blacklisted
   */
  @Command(aliases = "blacklist", description = "Black list an user from opening tickets")
  public Result blacklist(
      @Required(name = "user", description = "The user to blacklist") Member user) {
    // TODO
    return new Result();
  }
}
