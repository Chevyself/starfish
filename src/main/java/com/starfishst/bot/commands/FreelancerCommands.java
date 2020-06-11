package com.starfishst.bot.commands;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.DiscordConfiguration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.TicketCreationException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.freelancers.Offer;
import com.starfishst.bot.objects.management.AllowedTicketCloserChecker;
import com.starfishst.bot.objects.management.AllowedTicketManagerChecker;
import com.starfishst.bot.objects.questions.RoleAnswer;
import com.starfishst.bot.objects.responsive.type.freelancer.ReviewFreelancer;
import com.starfishst.bot.objects.responsive.type.quotes.OfferAcceptResponsiveMessage;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.tickets.type.Apply;
import com.starfishst.bot.tickets.type.FreelancingTicket;
import com.starfishst.bot.tickets.type.Quote;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Discord;
import com.starfishst.bot.util.Freelancers;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Exclude;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.message.MessageQuery;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.core.utils.Atomic;
import com.starfishst.core.utils.Lots;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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
      return new Result(member.getAsMention() + " has been promoted to freelancer");
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
    HashMap<String, String> placeholders = Freelancers.getPlaceholders(freelancer);
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
    return new Result(Lang.get("FREELANCER_DEMOTED", placeholders));
  }

  /**
   * Sends a quote to a quote ticket
   *
   * @param freelancer the freelancer sending the quote
   * @param id the id of the quote
   * @param strings the offer that the freelancer is sending
   * @return successful result if the quote is send ok
   */
  @Command(
      aliases = {"quote", "offer"},
      description = "Sends a quote to an order")
  public Result quote(
      Freelancer freelancer,
      @Required(name = "id", description = "The id of the ticket") long id,
      @Required(name = "quote", description = "The quote to send to the order") @Multiple
          JoinedStrings strings) {
    Ticket ticket = TicketManager.getInstance().getLoader().getTicket(id);
    String quote = strings.getString();
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
              placeholders.put("offer", quote);
              placeholders.put("quote", quote);
              MessageBuilder builder =
                  Messages.create(
                          "NEW_OFFER_TITLE", "NEW_OFFER_DESCRIPTION", placeholders, placeholders)
                      .getAsMessageQuery()
                      .getBuilder();
              if (ticket.getUser() != null) {
                builder.append(ticket.getMember());
              }
              new MessageQuery(builder)
                  .send(
                      ticket.getChannel(),
                      msg ->
                          ((Quote) ticket)
                              .addOffer(
                                  new Offer(
                                      freelancer, quote, new OfferAcceptResponsiveMessage(msg))));
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
   * @param channel the channel where the rating embed will be created
   * @return the message if it could be created
   */
  @Command(aliases = "review", description = "Makes a freelancer review for an user")
  public Result review(AllowedTicketManagerChecker checker, TextChannel channel) {
    Ticket ticket = TicketManager.getInstance().getLoader().getTicketByChannel(channel.getIdLong());
    if (ticket == null) {
      return new Result(ResultType.ERROR, Lang.get("NOT_TICKET_CHANNEL"));
    } else if (ticket instanceof FreelancingTicket) {
      try {
        Freelancer freelancer = ((FreelancingTicket) ticket).getFreelancer();
        User user = ticket.getUser();
        Member member = Discord.getMember(user);
        HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
        if (freelancer == null) {
          return new Result(ResultType.ERROR, Lang.get("TICKET_NOT_FREELANCER", placeholders));
        } else if (member == null) {
          return new Result(ResultType.ERROR, Lang.get("TICKET_NOT_USER", placeholders));
        } else {
          placeholders.putAll(Freelancers.getPlaceholders(freelancer));
          placeholders.put("user", member.getAsMention());
          placeholders.put("username", member.getEffectiveName());
          if (freelancer.getRating().containsKey(member.getIdLong())) {
            return new Result(ResultType.ERROR, Lang.get("ALREADY_RATED", placeholders));
          } else {
            return new Result(
                ResultType.GENERIC,
                Messages.create(
                    "FREELANCER_REVIEW_TITLE",
                    "FREELANCER_REVIEW_DESCRIPTION",
                    placeholders,
                    placeholders),
                msg -> new ReviewFreelancer(msg, freelancer.getId(), member.getUser().getIdLong()));
          }
        }
      } catch (DiscordManipulationException e) {
        return new Result(ResultType.ERROR, e.getMessage());
      }

    } else {
      return new Result(ResultType.USAGE, Lang.get("TICKET_NOT_REVIEWABLE"));
    }
  }

  /**
   * Shows information about a freelancer
   *
   * @param freelancer the freelancer to get info from
   * @return the message with information about the freelancer
   */
  @Exclude
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
              "PRODUCT_CREATED_TITLE", "PRODUCT_CREATED_DESCRIPTION", placeholders, placeholders));
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
      @Required(name = "portfolio", description = "The portfolio to set") @Multiple
          JoinedStrings strings) {
    List<String> portfolio = new ArrayList<>(Arrays.asList(strings.getString().split(", ")));
    freelancer.setPortfolio(portfolio);
    return new Result(
        Lang.get("FREELANCER_PORTFOLIO_SET"), msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
  }
}
