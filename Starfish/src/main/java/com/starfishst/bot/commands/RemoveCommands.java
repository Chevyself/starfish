package com.starfishst.bot.commands;

import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.FreelancerJoinTicketException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.management.AllowedTicketManagerChecker;
import com.starfishst.bot.oldtickets.TicketManager;
import com.starfishst.bot.oldtickets.loader.TicketLoader;
import com.starfishst.bot.oldtickets.type.FreelancingTicket;
import com.starfishst.bot.oldtickets.type.Ticket;
import com.starfishst.bot.util.Discord;
import com.starfishst.bot.util.Messages;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands for multiple tasks that are related to removing */
public class RemoveCommands {

  /**
   * Removes people from a channel. If the channel is a ticket and the freelancer is mentioned it
   * will also remove it
   *
   * @param user the user removing
   * @param message the message to get the mentioned people
   * @return a successfully message if people is mentioned
   */
  @Command(aliases = "remove", description = "Removes people from a channel/ticket")
  public Result remove(AllowedTicketManagerChecker user, Message message) {
    if (message.getMentionedMembers().isEmpty()) {
      return new Result(ResultType.USAGE, Lang.get("NO_MENTIONED_TO_REMOVE"));
    } else {
      TicketLoader loader = TicketManager.getInstance().getLoader();
      TextChannel channel = message.getTextChannel();
      Ticket ticket = loader.getTicketByChannel(channel.getIdLong());

      if (ticket instanceof FreelancingTicket) {
        message
            .getMentionedMembers()
            .forEach(
                member -> {
                  Freelancer freelancer = loader.getFreelancer(member.getIdLong());
                  if (freelancer != null) {
                    if (((FreelancingTicket) ticket).getFreelancer() == freelancer) {
                      try {
                        ((FreelancingTicket) ticket).setFreelancer(null);
                      } catch (FreelancerJoinTicketException | DiscordManipulationException e) {
                        Messages.error(e.getMessage()).send(channel);
                      }
                    }
                  }
                });
      }

      Discord.disallow(channel, message.getMentionedMembers());
      return new Result(Lang.get("MEMBERS_REMOVED"));
    }
  }
}
