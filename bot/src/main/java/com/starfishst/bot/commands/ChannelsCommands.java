package com.starfishst.bot.commands;

import com.starfishst.api.Starfish;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Discord;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
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
  @Command(aliases = "add", description = "add.description", node = "starfish.add")
  public Result add(BotUser user, Member member, Message message, CommandContext context) {
    List<IMentionable> mentions = new ArrayList<>(message.getMentionedRoles());
    mentions.addAll(message.getMentionedMembers());
    GuildChannel channel = message.getTextChannel();
    Loader loader = Starfish.getLoader();
    if (mentions.isEmpty()) {
      return new Result(ResultType.USAGE, user.getLocaleFile().get("add.mentions-empty"));
    }
    Optional<? extends Ticket> optionalTicket = loader.getTicketByChannel(channel.getIdLong());
    if (!optionalTicket.isPresent()) {
      GuildVoiceState state = member.getVoiceState();
      if (state != null && state.getChannel() != null) {
        channel = state.getChannel();
      }
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
      Ticket ticket = optionalTicket.get();
      for (IMentionable mentionable : mentions) {
        if (mentionable instanceof Member) {
          BotUser starfishUser = loader.getStarfishUser(mentionable.getIdLong());
          if (starfishUser.isFreelancer()) {
            ticket.addUser(starfishUser, "freelancer");
          } else {
            ticket.addUser(starfishUser, "customer");
          }
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
  @Command(aliases = "remove", description = "remove.description", node = "starfish.remove")
  public Result remove(BotUser user, Message message, CommandContext context) {
    List<IMentionable> mentions = new ArrayList<>(message.getMentionedRoles());
    mentions.addAll(message.getMentionedMembers());
    TextChannel channel = message.getTextChannel();
    Loader loader = Starfish.getLoader();
    if (mentions.isEmpty()) {
      return new Result(ResultType.USAGE, user.getLocaleFile().get("remove.mentions-empty"));
    }
    Optional<? extends Ticket> optionalTicket = loader.getTicketByChannel(channel.getIdLong());
    if (!optionalTicket.isPresent()) {
      for (IMentionable mentionable : mentions) {
        if (mentionable instanceof IPermissionHolder) {
          Discord.disallow(channel, (IPermissionHolder) mentionable);
        }
      }
      return new Result(user.getLocaleFile().get("remove.members-remove"));
    } else {
      Ticket ticket = optionalTicket.get();
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
