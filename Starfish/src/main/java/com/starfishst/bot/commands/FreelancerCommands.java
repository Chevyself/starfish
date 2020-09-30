package com.starfishst.bot.commands;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.data.StarfishFreelancer;
import com.starfishst.bot.data.StarfishUser;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.data.messages.offer.OfferMessage;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.core.utils.maps.Maps;
import java.util.HashMap;

import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/** Commands related to freelancing */
public class FreelancerCommands {

  /**
   * Promotes an user to freelancer
   *
   * @param sender the user promoting the freelancer
   * @param user the user that is not a freelancer
   * @return the result of the promotion
   */
  @Command(
      aliases = "promote",
      description = "promote.desc",
      permission = @Perm(node = "starfish.promote"))
  public Result promote(
      BotUser sender, @Required(name = "user", description = "The user to promote") BotUser user) {
    HashMap<String, @NotNull String> placeholders = user.getPlaceholders();
    if (!user.getPreferences().getValueOr("freelancer", Boolean.class, false)) {
      new StarfishFreelancer(user);
      return new Result(sender.getLocaleFile().get("user.promoted", placeholders));
    } else {
      return new Result(sender.getLocaleFile().get("user.already-freelancer", placeholders));
    }
  }

  /**
   * Demotes an user
   *
   * @param sender the command executor
   * @param user the user to demote
   * @return the result of the command
   */
  @Command(aliases = "demote", description = "demote.desc", permission = @Perm(node = "starfish.demote"))
  public Result demote(BotUser sender, @Required(name = "user", description = "demote.desc") BotUser user) {
    HashMap<String, @NotNull String> placeholders = user.getPlaceholders();
    if (user.getPreferences().getValueOr("freelancer", Boolean.class, false)) {
      new StarfishUser(user);
      return new Result(sender.getLocaleFile().get("user.demoted", placeholders));
    } else {
      return new Result(sender.getLocaleFile().get("user.is-not-freelancer", placeholders));
    }
  }

  /**
   * See the information of a freelancer
   *
   * @param sender the user that wants to see the information
   * @param user the user that is supposed to be a freelancer
   * @return the result of the freelancer information if the user happens to be a freelancer
   */
  @Command(
      aliases = {"freelancerinfo", "fi"},
      description = "freelancerinfo.desc",
      permission = @Perm(node = "starfish.freelancerinfo"))
  public Result freelancerInfo(
      BotUser sender,
      @Required(name = "freelancer", description = "The freelancer to view") BotUser user) {
    if (user.getPreferences().getValueOr("freelancer", Boolean.class, false)) {
      return new Result(user.toCompleteInformation(sender));
    } else {
      return new Result(
          sender
              .getLocaleFile()
              .get(
                  "freelancer-info.user.not-freelancer",
                  Maps.singleton("user", sender.getMention())));
    }
  }

  /**
   * Send a quote to a quote ticket
   * @param freelancer the freelancer sending the quote
   * @param ticket the ticket that is getting the quote
   * @param strings the quote
   * @return the result of the command
   */
  @Command(aliases = {"quote", "offer"}, description = "quote.desc")
  public Result quote(StarfishFreelancer freelancer, @Required(name = "quote.ticket", description = "quote.ticket.desc") Ticket ticket, @Multiple @Required(name = "quote.offer", description = "quote.offer.desc")JoinedStrings strings) {
    LocaleFile locale = freelancer.getLocaleFile();
    if (ticket.getTicketType() != TicketType.QUOTE) {
      return new Result(locale.get("quote.ticket-not-quote", ticket.getPlaceholders()));
    } else {
      TextChannel channel = ticket.getTextChannel();
      if (ticket.getTicketStatus() == TicketStatus.CLOSED && channel == null) {
        return new Result(locale.get("quote.closed", ticket.getPlaceholders()));
      } else if (channel != null && ticket.getTicketStatus() == TicketStatus.OPEN) {
        HashMap<String, String> placeholders = Maps.singleton("offer", strings.toString());
        BotUser owner = ticket.getOwner();
        if (owner != null) {
          Messages.build(owner.getLocaleFile().get("offer.title", placeholders), owner.getLocaleFile().get("offer.desc", placeholders), ResultType.GENERIC, owner).send(channel, msg -> {
            StarfishValuesMap map = new StarfishValuesMap();
            map.addValue("offer", strings.toString());
            map.addValue("freelancer", freelancer.getId());
            new OfferMessage(msg, map);
          });
        }
      }
    }
    return new Result(locale.get("quote.sent"));
  }
}
