package com.starfishst.ethot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Optional;
import com.starfishst.commands.annotations.Required;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.core.utils.Atomic;
import com.starfishst.core.utils.Lots;
import com.starfishst.ethot.config.Configuration;
import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.freelancers.Offer;
import com.starfishst.ethot.objects.management.AllowedTicketCloserChecker;
import com.starfishst.ethot.objects.management.AllowedTicketManagerChecker;
import com.starfishst.ethot.objects.questions.RoleAnswer;
import com.starfishst.ethot.objects.responsive.type.freelancer.ReviewFreelancer;
import com.starfishst.ethot.objects.responsive.type.quotes.OfferAcceptResponsiveMessage;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.Apply;
import com.starfishst.ethot.tickets.type.Quote;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Freelancers;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Tickets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands related to freelancers */
public class FreelancerCommands {

  /**
   * Promotes a freelancer
   *
   * @param member the member to promote as freelancer
   * @return a successful result if the member was not a freelancer
   */
  @Command(
      aliases = "promote",
      description = "Promotes a Freelancer",
      permission = Permission.ADMINISTRATOR)
  public Result promote(
      @Required(name = "member", description = "The member to promote as Freelancer")
          Member member) {
    Freelancer freelancer =
        TicketManager.getInstance().getLoader().getFreelancer(member.getIdLong());
    if (freelancer != null) {
      return new Result(ResultType.USAGE, member.getAsMention() + " is already a freelancer!");
    } else {
      new Freelancer(new ArrayList<>(), new HashMap<>(), member.getIdLong());
      return new Result(
          member.getAsMention() + " has been promoted to freelancer",
          msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }
  }

  /**
   * Accepts an application. Basically gives the roles that were mentioned in the answers to the
   * accepted user
   *
   * @param checker checks the user that can use the command
   * @param channel the channel where it was executed
   * @return a success if everything goes as planned
   */
  @Command(aliases = "accept", description = "Accepts an application")
  public Result accept(AllowedTicketCloserChecker checker, TextChannel channel) {
    Ticket ticket = TicketManager.getInstance().getLoader().getTicketByChannel(channel.getIdLong());
    if (ticket == null) {
      return new Result(ResultType.ERROR, Lang.get("NOT_TICKET_CHANNEL"));
    } else if (ticket instanceof Apply) {
      Atomic<Boolean> success = new Atomic<>(false);
      List<Role> given = new ArrayList<>();
      ((Apply) ticket)
          .getAnswers()
          .forEach(
              (simple, answer) -> {
                if (answer instanceof RoleAnswer) {
                  if (ticket.getUser() != null) {
                    try {
                      Member member = Discord.getMember(ticket.getUser());
                      if (member != null) {
                        Discord.addRoles(member, ((RoleAnswer) answer).getAnswer());
                        given.addAll(((RoleAnswer) answer).getAnswer());
                        success.set(true);
                      } else {
                        Messages.error(
                                Lang.get("TICKET_NOT_MEMBER", Tickets.getPlaceholders(ticket)))
                            .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
                        success.set(false);
                      }
                    } catch (DiscordManipulationException e) {
                      Messages.error(e.getMessage())
                          .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
                      success.set(false);
                    }
                  } else {
                    Messages.error(Lang.get("TICKET_NOT_USER", Tickets.getPlaceholders(ticket)))
                        .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
                    success.set(false);
                  }
                }
              });
      if (success.get()) {
        HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
        placeholders.put("given", Lots.pretty(Discord.getAsMention(given)));
        return new Result(Lang.get("APPLICATION_ACCEPTED", placeholders));
      } else {
        return new Result();
      }
    } else {
      return new Result(
          ResultType.ERROR, Lang.get("NOT_AN_APPLICATION", Tickets.getPlaceholders(ticket)));
    }
  }

  /**
   * Demotes a freelancer
   *
   * @param channel the channel to send errors messages in case of one
   * @param freelancer the freelancer to demote
   * @param removeRoles do we remove the roles of the freelancer?
   * @return a successful result if the freelancer gets demoted
   * @throws DiscordManipulationException in case working with discord goes wrong
   */
  @Command(
      aliases = "demote",
      description = "Demotes a Freelancer",
      permission = Permission.ADMINISTRATOR)
  public Result promote(
      TextChannel channel,
      @Required(name = "freelancer", description = "The freelancer to demote")
          Freelancer freelancer,
      @Optional(
              name = "remove roles",
              description = "Do we remove the roles of the freelancer?",
              suggestions = "false")
          boolean removeRoles)
      throws DiscordManipulationException {
    TicketManager.getInstance().getLoader().demoteFreelancer(freelancer);
    if (removeRoles && freelancer.getMember() != null) {
      List<Role> roles = new ArrayList<>(freelancer.getMember().getRoles());
      roles.forEach(
          role -> {
            try {
              DiscordConfiguration.getInstance()
                  .getGuild()
                  .removeRoleFromMember(freelancer.getMember(), role)
                  .queue();
            } catch (DiscordManipulationException e) {
              Messages.error(e.getMessage()).send(channel);
            }
          });
    }
    return new Result(
        ResultType.GENERIC,
        Messages.create("FREELANCER_DEMOTED_TITLE", "FREELANCER_DEMOTED_DESCRIPTION", null, null),
        msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
  }

  /**
   * Sends a quote to a quote ticket
   *
   * @param freelancer the freelancer sending the quote
   * @param id the id of the quote
   * @param quote the offer that the freelancer is sending
   * @return successful result if the quote is send ok
   */
  @Command(aliases = "quote", description = "Sends a quote to an order")
  public Result quote(
      Freelancer freelancer,
      @Required(name = "id", description = "The id of the ticket") long id,
      @Required(name = "quote", description = "The quote to send to the order") double quote) {
    Ticket ticket = TicketManager.getInstance().getLoader().getTicket(id);
    if (!(ticket instanceof Quote)) {
      if (ticket != null) {
        return new Result(
            ResultType.USAGE, Lang.get("NO_QUOTABLE", Tickets.getPlaceholders(ticket)));
      } else {
        return new Result(ResultType.USAGE, Lang.get("TICKET_NULL"));
      }
    } else if (ticket.getChannel() == null) {
      return new Result(ResultType.ERROR, Lang.get("NO_CHANNEL", Tickets.getPlaceholders(ticket)));
    } else if (freelancer.getUser() == null) {
      return new Result(ResultType.UNKNOWN, "This should not have happened... Your user is null");
    } else {
      try {
        if (freelancer.hasRole(((Quote) ticket).getAnswers())) {
          if (((Quote) ticket).getFreelancer() == null) {
            long limit = Configuration.getInstance().getLimitOfQuotes();
            HashMap<String, String> placeholders = Freelancers.getPlaceholders(freelancer);
            if (((Quote) ticket).countOffers(freelancer) < limit) {
              placeholders.put("offer", String.valueOf(quote));
              placeholders.put("quote", String.valueOf(quote));
              Messages.create(
                      "NEW_OFFER_TITLE", "NEW_OFFER_DESCRIPTION", placeholders, placeholders)
                  .send(
                      ticket.getChannel(),
                      msg ->
                          ((Quote) ticket)
                              .addOffer(
                                  new Offer(
                                      freelancer,
                                      String.valueOf(quote),
                                      new OfferAcceptResponsiveMessage(msg))));
              return new Result(
                  ResultType.GENERIC, Lang.get("OFFER_SENT", Tickets.getPlaceholders(ticket)));
            } else {
              placeholders.put("limit", String.valueOf(limit));
              return new Result(ResultType.ERROR, Lang.get("NO_MORE_QUOTES", placeholders));
            }
          } else {
            return new Result(
                ResultType.ERROR,
                Lang.get("QUOTE_HAS_FREELANCER", Tickets.getPlaceholders(ticket)));
          }
        } else {
          freelancer.sendMessage(Messages.error(Lang.get("FREELANCER_NO_ROLE")));
          return new Result();
        }
      } catch (DiscordManipulationException e) {
        return new Result(ResultType.ERROR, e.getMessage());
      }
    }
  }

  /**
   * Creates a message for the user to react and give a rating to a freelancer
   *
   * @param checker the check to see if the user executed the command can create the message
   * @param member the member that will give a rating to the freelancer
   * @param freelancer the freelancer that will receive the rating
   * @return the message if it could be created
   */
  @Command(aliases = "review", description = "Makes a freelancer review for an user")
  public Result review(
      AllowedTicketManagerChecker checker,
      @Required(name = "user", description = "The user rating the freelancer") Member member,
      @Required(name = "freelancer", description = "The freelancer to rate")
          Freelancer freelancer) {
    HashMap<String, String> placeholders = Freelancers.getPlaceholders(freelancer);
    placeholders.put("user", member.getAsMention());
    placeholders.put("username", member.getEffectiveName());
    return new Result(
        ResultType.GENERIC,
        Messages.create(
            "FREELANCER_REVIEW_TITLE", "FREELANCER_REVIEW_DESCRIPTION", placeholders, placeholders),
        msg -> new ReviewFreelancer(msg, freelancer.getId(), member.getUser().getIdLong()));
  }

  /**
   * Shows information about a freelancer
   *
   * @param freelancer the freelancer to get info from
   * @return the message with information about the freelancer
   */
  @Command(
      aliases = {"freelancerinfo", "fi"},
      description = "See the freelancer info")
  public Result info(
      @Required(name = "freelancer", description = "The freelancer you want info from")
          Freelancer freelancer) {
    HashMap<String, String> placeholders = Freelancers.getPlaceholders(freelancer);
    return new Result(
        ResultType.GENERIC,
        Messages.create(
            "FREELANCER_INFO_TITLE", "FREELANCER_INFO_DESCRIPTION", placeholders, placeholders));
  }

  /**
   * Lets a freelancer create a product
   *
   * @param freelancer the freelancer wanting to create the product
   * @return a successful message if the ticket was created
   * @throws DiscordManipulationException in case that creating the channel goes wrong
   * @throws TicketCreationException in case that the freelancer cannot open the ticket
   */
  @Command(aliases = "sell", description = "Sell a product!")
  public Result sell(Freelancer freelancer)
      throws DiscordManipulationException, TicketCreationException {
    if (freelancer.getMember() != null) {
      Ticket ticket =
          TicketManager.getInstance()
              .createTicket(TicketType.PRODUCT, freelancer.getMember(), null);
      HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
      return new Result(
          ResultType.GENERIC,
          Messages.create(
              "PRODUCT_CREATED_TITLE", "PRODUCT_CREATED_DESCRIPTION", placeholders, placeholders),
          msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
    } else {
      return new Result(
          ResultType.UNKNOWN, "This should not have happened freelancer member is null!");
    }
  }

  /**
   * Sets the portfolio of a freelancer
   *
   * @param freelancer the freelancer to set the portfolio
   * @param strings the portfolio to set
   * @return a successful result showing that the portfolio changed
   */
  @Command(aliases = "portfolio", description = "Let's a freelancer edit their portfolio")
  public Result portfolio(
      Freelancer freelancer,
      @Required(name = "portfolio", description = "The portfolio to set") JoinedStrings strings) {
    List<String> portfolio = new ArrayList<>(Arrays.asList(strings.getString().split(", ")));
    freelancer.setPortfolio(portfolio);
    return new Result(
        Lang.get("FREELANCER_PORTFOLIO_SET"), msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
  }
}
