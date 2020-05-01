package com.starfishst.ethot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Optional;
import com.starfishst.commands.annotations.Required;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.config.PunishmentsConfiguration;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Discord;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/** Commands related to moderation */
@Deprecated
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
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("user", user.getEffectiveName());
    PunishmentsConfiguration configuration = PunishmentsConfiguration.getInstance();
    if (configuration.isMuted(user.getUser())) {
      return new Result(ResultType.ERROR, Lang.get("USER_ALREADY_MUTED", placeholders));
    } else {
      configuration.mute(user.getUser());
      return new Result(
          Lang.get("USER_HAS_BEEN_MUTED", placeholders),
          msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
    }
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
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("user", user.getEffectiveName());
    PunishmentsConfiguration configuration = PunishmentsConfiguration.getInstance();
    if (configuration.isMuted(user.getUser())) {
      configuration.unmute(user.getUser());
      return new Result(
          Lang.get("USER_UNMUTED", placeholders),
          msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
    } else {
      return new Result(ResultType.ERROR, Lang.get("USER_NOT_MUTED", placeholders));
    }
  }

  /**
   * Ban a specified user
   *
   * @param user the user to ban
   * @param days delete the messages sent by the user in the specified days
   * @param reason the reason to ban it
   * @return a successful result if the user was banned
   * @throws DiscordManipulationException if the guild has not been set
   */
  @Command(
      aliases = "ban",
      description = "Ban the specified user",
      permission = Permission.ADMINISTRATOR)
  public Result ban(
      @Required(name = "user", description = "The user to ban") Member user,
      @Required(
              name = "days",
              description = "Delete the messages sent by the user based on the number of days")
          int days,
      @Required(name = "reason", description = "The reason of the ban") JoinedStrings reason)
      throws DiscordManipulationException {
    DiscordConfiguration.getInstance().getGuild().ban(user, days, reason.getString()).queue();
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("banned", user.getUser().getAsTag());
    placeholders.put("days", String.valueOf(days));
    placeholders.put("reason", reason.getString());
    return new Result(Lang.get("USER_BANNED", placeholders));
  }

  /**
   * Unbans an specified user
   *
   * @param user the user to unban
   * @return a successful result if the user was unbanned
   * @throws DiscordManipulationException in case that the guild is not set
   */
  @Command(
      aliases = {"unban", "pardon"},
      description = "Unban the specified user",
      permission = Permission.ADMINISTRATOR)
  public Result unban(@Required(name = "user", description = "The user to unban") User user)
      throws DiscordManipulationException {
    HashMap<String, String> placeholders = new HashMap<>();
    if (Discord.isBanned(user)) {
      DiscordConfiguration.getInstance().getGuild().unban(user).queue();
      placeholders.put("unbanned", user.getAsTag());
      return new Result(
          Lang.get("USER_UNBANNED", placeholders),
          msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
    } else {
      placeholders.put("user", user.getAsTag());
      return new Result(ResultType.ERROR, Lang.get("USER_IS_NOT_BANNED", placeholders));
    }
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
    Ticket ticket = TicketManager.getInstance().getLoader().getTicketByChannel(channel.getIdLong());
    if (ticket != null) {
      return new Result(ResultType.ERROR, Lang.get("CANNOT_CLEAR_INSIDE_TICKETS"));
    } else {
      if (amount > 100) {
        return new Result(ResultType.ERROR, Lang.get("CANNOT_CLEAR_MORE_THAN_100"));
      } else {
        channel
            .getHistory()
            .retrievePast(amount)
            .queue(messages -> messages.forEach(message -> message.delete().queue()));
        return new Result(
            Lang.get("MESSAGES_CLEARED"), msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
      }
    }
  }
}
