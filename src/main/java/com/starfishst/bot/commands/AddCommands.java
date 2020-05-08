package com.starfishst.bot.commands;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.FreelancerJoinTicketException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.management.AllowedTicketManagerChecker;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.loader.TicketLoader;
import com.starfishst.bot.tickets.type.FreelancingTicket;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Discord;
import com.starfishst.bot.util.Messages;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/** Multiple commands related to 'adding' */
public class AddCommands {

  /**
   * Adds the mentioned members into a channel. Also if the channel is a ticket and one of the
   * mentioned members is a freelancer it will add them to the ticket
   *
   * @param checker to check if the user is allowed to add people to the channel
   * @param message the message to get the members to
   * @return successful result if there's mentioned members
   */
  @Command(aliases = "add", description = "Adds people to a channel or a ticket")
  public Result add(AllowedTicketManagerChecker checker, Message message) {
    if (message.getMentionedMembers().isEmpty()) {
      return new Result(ResultType.USAGE, Lang.get("NO_MENTIONED_TO_ADD"));
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
                    try {
                      ((FreelancingTicket) ticket).setFreelancer(freelancer);
                    } catch (FreelancerJoinTicketException | DiscordManipulationException e) {
                      Messages.error(e.getMessage()).send(channel);
                    }
                  }
                });
      }

      Discord.allow(channel, message.getMentionedMembers(), Discord.ALLOWED);
      return new Result(Lang.get("MEMBERS_ADDED"));
    }
  }
}
