package com.starfishst.bot.commands;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.utility.Discord;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.tickets.StarfishLoader;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.List;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands used for addition */
public class ChannelsCommands {

  /**
   * Adds users and roles (any {@link IPermissionHolder}) to a channel or ticket
   *
   * @param user the user executing the command
   * @param message the message to get the mentions
   * @param context the context of the command
   * @return the result of the command execution
   */
  @Command(
      aliases = "add",
      description = "add.description",
      permission = @Perm(node = "starfish.add"))
  public Result add(BotUser user, Message message, CommandContext context) {
    List<IMentionable> mentions =
        message.getMentions(Message.MentionType.ROLE, Message.MentionType.USER);
    TextChannel channel = message.getTextChannel();
    StarfishLoader loader = Starfish.getLoader();
    if (mentions.isEmpty()) {
      return new Result(ResultType.USAGE, user.getLocaleFile().get("add.mentions-empty"));
    }
    Ticket ticket = loader.getTicketByChannel(message.getIdLong());
    if (ticket == null) {
      if (context.hasFlag("-v")) {
        for (IMentionable mentionable : mentions) {
          if (mentionable instanceof IPermissionHolder) {
            Discord.allow(channel, (IPermissionHolder) mentionable, Discord.ALLOWED_SEE);
          }
        }
      } else {
        for (IMentionable mentionable : mentions) {
          if (mentionable instanceof IPermissionHolder) {
            Discord.allow(channel, (IPermissionHolder) mentionable, Discord.ALLOWED);
          }
        }
      }
      return new Result(user.getLocaleFile().get("add.members-added"));
    } else {
      for (IMentionable mentionable : mentions) {
        if (mentionable instanceof Member) {
          BotUser starfishUser = loader.getStarfishUser(mentionable.getIdLong());
          ticket.addUser(starfishUser, "customer");
        } else {
          if (context.hasFlag("-v")) {
            if (mentionable instanceof IPermissionHolder) {
              Discord.allow(channel, (IPermissionHolder) mentionable, Discord.ALLOWED_SEE);
            }
          } else {
            if (mentionable instanceof IPermissionHolder) {
              Discord.allow(channel, (IPermissionHolder) mentionable, Discord.ALLOWED);
            }
          }
        }
      }
      return new Result(
          user.getLocaleFile().get("add.members-added-to-ticket", ticket.getPlaceholders()));
    }
  }

  /**
   * Remove users and members from a ticket or channel
   *
   * @param user the user removing
   * @param message the message to get the mentions
   * @param context the context of the message
   * @return the result of the command execution
   */
  @Command(
      aliases = "remove",
      description = "remove.description",
      permission = @Perm(node = "starfish.remove"))
  public Result remove(BotUser user, Message message, CommandContext context) {
    List<IMentionable> mentions =
        message.getMentions(Message.MentionType.ROLE, Message.MentionType.USER);
    TextChannel channel = message.getTextChannel();
    StarfishLoader loader = Starfish.getLoader();
    if (mentions.isEmpty()) {
      return new Result(ResultType.USAGE, user.getLocaleFile().get("remove.mentions-empty"));
    }
    Ticket ticket = loader.getTicketByChannel(message.getIdLong());
    if (ticket == null) {
      for (IMentionable mentionable : mentions) {
        if (mentionable instanceof IPermissionHolder) {
          Discord.disallow(channel, (IPermissionHolder) mentionable);
        }
      }
      return new Result(user.getLocaleFile().get("remove.members-remove"));
    } else {
      for (IMentionable mentionable : mentions) {
        if (mentionable instanceof Member) {
          BotUser starfishUser = loader.getStarfishUser(mentionable.getIdLong());
          ticket.removeUser(starfishUser);
        } else {
          if (mentionable instanceof IPermissionHolder) {
            Discord.disallow(channel, (IPermissionHolder) mentionable);
          }
        }
      }
      return new Result(
          user.getLocaleFile().get("remove.members-remove-to-ticket", ticket.getPlaceholders()));
    }
  }
}
