package com.starfishst.ethot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.config.objects.management.AllowedTicketManagerChecker;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.FreelancerJoinTicketException;
import com.starfishst.ethot.tickets.loader.TicketLoader;
import com.starfishst.ethot.tickets.type.FreelancingTicket;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Messages;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

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
      TicketLoader loader = Main.getManager().getLoader();
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
      return new Result(
          Lang.get("MEMBERS_ADDED"), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
    }
  }
}
