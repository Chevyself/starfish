package com.starfishst.bot.commands;

import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.commands.objects.Freelancer;
import com.starfishst.bot.data.StarfishTicket;
import com.starfishst.bot.messages.OfferAcceptReactionResponse;
import com.starfishst.bot.messages.ReviewReactionResponse;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import me.googas.commands.annotations.Multiple;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import me.googas.commands.objects.JoinedStrings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
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
      user.getPreferences().add("freelancer", true);
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
    if (user.getPreferences().getOr("freelancer", Boolean.class, false)) {
      user.getPreferences().remove("freelancer");
      user.getPreferences().remove("portfolio");
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
    Result.ResultBuilder resultBuilder = Result.builder();
    resultBuilder.getMessage().setEmbeds(freelancer.toCompleteInformation(sender).build());
    return resultBuilder.build();
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
      Optional<TextChannel> optionalChannel = ticket.getTextChannel();
      if (ticket.getStatus() == TicketStatus.CLOSED && !optionalChannel.isPresent()) {
        return new Result(locale.get("quote.closed", ticket.getPlaceholders()));
      } else if (optionalChannel.isPresent() && ticket.getStatus() == TicketStatus.OPEN) {
        TextChannel channel = optionalChannel.get();
        Map<String, String> placeholders = Collections.singletonMap("offer", strings.getString());
        ticket
            .getOwner()
            .ifPresent(
                owner -> {
                  EmbedBuilder embedBuilder =
                      Messages.build(
                          owner.getLocaleFile().get("offer.title", placeholders),
                          owner.getLocaleFile().get("offer.desc", placeholders),
                          ResultType.GENERIC,
                          owner);
                  MessageBuilder messageBuilder =
                      new MessageBuilder(embedBuilder).append(channel.getGuild().getPublicRole());
                  channel
                      .sendMessage(messageBuilder.build())
                      .queue(
                          msg -> {
                            ValuesMap map =
                                new ValuesMap()
                                    .add("offer", strings.getString())
                                    .add("freelancer", freelancer.getId())
                                    .add("ticket", ticket.getId());
                            BotResponsiveMessage responsiveMessage =
                                new BotResponsiveMessage(msg.getIdLong()).cache();
                            responsiveMessage.addReactionResponse(
                                new OfferAcceptReactionResponse(responsiveMessage), msg);
                          });
                });
      }
    }
    return new Result(locale.get("quote.sent", ticket.getPlaceholders()));
  }

  @Command(aliases = "review", description = "freelancer.review.desc", node = "starfish.review")
  public Result review(LocaleFile locale, Ticket ticket) {
    if (!(ticket instanceof StarfishTicket))
      return new Result(ResultType.ERROR, "This ticket is not an starfish ticket");
    Optional<BotUser> optionalOwner = ticket.getOwner();
    if (!optionalOwner.isPresent())
      return new Result(ResultType.ERROR, locale.get("review.ticket-no-user"));
    Optional<Member> optionalMember = optionalOwner.get().getMember();
    if (!optionalMember.isPresent())
      return new Result(ResultType.ERROR, locale.get("review.ticket-no-user"));
    if (!ticket.hasFreelancers())
      return new Result(ResultType.ERROR, locale.get("review.ticket-no-freelancer"));
    BotUser owner = optionalOwner.get();
    Member member = optionalMember.get();
    BotUser freelancer = ticket.getFreelancer();
    Map<String, String> placeholders = freelancer.getPlaceholders();
    placeholders.put("user", member.getAsMention());
    LocaleFile ownerLocale = owner.getLocaleFile();
    Result.ResultBuilder resultBuilder = Result.builder();
    resultBuilder
        .getMessage()
        .setEmbeds(
            Messages.build(
                    ownerLocale.get("review.title", placeholders),
                    ownerLocale.get("review.description", placeholders),
                    ResultType.GENERIC,
                    owner)
                .build());
    return resultBuilder
        .next(
            msg -> {
              ReviewReactionResponse.add(
                      new BotResponsiveMessage(
                          msg.getIdLong(),
                          new ValuesMap("freelancer", freelancer.getId())
                              .add("user", owner.getId())),
                      msg)
                  .cache();
            })
        .build();
  }
}
