package com.starfishst.bot.commands;

import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.commands.objects.Freelancer;
import com.starfishst.bot.data.StarfishTicket;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.messages.OfferAcceptReactionResponse;
import com.starfishst.bot.messages.ReviewReactionResponse;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.message.MessageQuery;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands related to freelancing */
public class FreelancerCommands {

  /**
   * Promotes an user to freelancer
   *
   * @param sender the user promoting the freelancer
   * @param user the user that is not a freelancer
   * @return the result of the promotion
   */
  @Command(aliases = "promote", description = "promote.desc", node = "starfish.promote")
  public Result promote(
      BotUser sender, @Required(name = "user", description = "The user to promote") BotUser user) {
    Map<String, @NonNull String> placeholders = user.getPlaceholders();
    if (!user.isFreelancer()) {
      user.getPreferences().addValue("freelancer", true);
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
  @Command(aliases = "demote", description = "demote.desc", node = "starfish.demote")
  public Result demote(
      BotUser sender, @Required(name = "user", description = "demote.desc") BotUser user) {
    Map<String, @NonNull String> placeholders = user.getPlaceholders();
    if (user.getPreferences().getValueOr("freelancer", Boolean.class, false)) {
      user.getPreferences().removeValue("freelancer");
      user.getPreferences().removeValue("portfolio");
      return new Result(sender.getLocaleFile().get("user.demoted", placeholders));
    } else {
      return new Result(sender.getLocaleFile().get("user.is-not-freelancer", placeholders));
    }
  }

  /**
   * See the information of a freelancer
   *
   * @param sender the user that wants to see the information
   * @param freelancer the freelancer to view the information from
   * @return the result of the freelancer information if the user happens to be a freelancer
   */
  @Command(
      aliases = {"freelancerinfo", "fi"},
      description = "freelancerinfo.desc",
      node = "starfish.freelancerinfo")
  public Result freelancerInfo(
      BotUser sender,
      @Required(name = "freelancer", description = "The freelancer to view")
          Freelancer freelancer) {
    return new Result(freelancer.toCompleteInformation(sender));
  }

  /**
   * Send a quote to a quote ticket
   *
   * @param freelancer the freelancer sending the quote
   * @param ticket the ticket that is getting the quote
   * @param strings the quote
   * @return the result of the command
   */
  @Command(
      aliases = {"quote", "offer"},
      description = "quote.desc")
  public Result quote(
      Freelancer freelancer,
      @Required(name = "quote.ticket", description = "quote.ticket.desc") Ticket ticket,
      @Multiple @Required(name = "quote.offer", description = "quote.offer.desc")
          JoinedStrings strings) {
    LocaleFile locale = freelancer.getLocaleFile();
    if (ticket.getType() != TicketType.QUOTE) {
      return new Result(locale.get("quote.ticket-not-quote", ticket.getPlaceholders()));
    } else {
      TextChannel channel = ticket.getTextChannel();
      if (ticket.getStatus() == TicketStatus.CLOSED && channel == null) {
        return new Result(locale.get("quote.closed", ticket.getPlaceholders()));
      } else if (channel != null && ticket.getStatus() == TicketStatus.OPEN) {
        Map<String, String> placeholders = Maps.singleton("offer", strings.getString());
        BotUser owner = ticket.getOwner();
        if (owner != null) {
          MessageQuery query =
              Messages.build(
                      owner.getLocaleFile().get("offer.title", placeholders),
                      owner.getLocaleFile().get("offer.desc", placeholders),
                      ResultType.GENERIC,
                      owner)
                  .getAsMessageQuery();
          query.getBuilder().append(channel.getGuild().getPublicRole());
          query.send(
              channel,
              msg -> {
                StarfishValuesMap map = new StarfishValuesMap();
                map.addValue("offer", strings.getString());
                map.addValue("freelancer", freelancer.getId());
                map.addValue("ticket", ticket.getId());
                BotResponsiveMessage responsiveMessage = new BotResponsiveMessage(
                        msg.getIdLong())
                        .cache();
                responsiveMessage.addReactionResponse(new OfferAcceptReactionResponse(responsiveMessage), msg);
              });
        }
      }
    }
    return new Result(locale.get("quote.sent", ticket.getPlaceholders()));
  }

  @Command(aliases = "review", description = "freelancer.review.desc", node = "starfish.review")
  public Result review(LocaleFile locale, Ticket ticket) {
    if (!(ticket instanceof StarfishTicket))
      return new Result(ResultType.ERROR, "This ticket is not an starfish ticket");
    BotUser owner = ticket.getOwner();
    if (owner == null) return new Result(ResultType.ERROR, locale.get("review.ticket-no-user"));
    Member member = owner.getMember();
    if (member == null) return new Result(ResultType.ERROR, locale.get("review.ticket-no-user"));
    if (!ticket.hasFreelancers())
      return new Result(ResultType.ERROR, locale.get("review.ticket-no-freelancer"));
    BotUser freelancer = ticket.getFreelancer();
    Map<String, String> placeholders = freelancer.getPlaceholders();
    placeholders.put("user", member.getAsMention());
    LocaleFile ownerLocale = owner.getLocaleFile();
    return new Result(
        Messages.build(
            ownerLocale.get("review.title", placeholders),
            ownerLocale.get("review.description", placeholders),
            ResultType.GENERIC,
            owner),
        msg ->
            ReviewReactionResponse.add(
                    new BotResponsiveMessage(
                        msg.getIdLong(),
                        new StarfishValuesMap("freelancer", freelancer.getId())
                            .addValue("user", owner.getId())),
                    msg)
                .cache());
  }
}
