package com.starfishst.bot.tickets.type;

import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.TicketCreationException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.questions.Answer;
import com.starfishst.bot.objects.questions.Question;
import com.starfishst.bot.objects.questions.StringAnswer;
import com.starfishst.bot.objects.responsive.type.orders.OrderClaimingResponsiveMessage;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.bot.util.Messages;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.Strings;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Order is a ticket that has a freelancer and a customer */
public class Order extends FreelancingTicket {

  /** The message waiting for the order to be claimed */
  @Nullable private OrderClaimingResponsiveMessage message;

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param user the user that created the ticket
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param details the details given by the customer
   * @param freelancer the freelancer that is in the order
   * @param message the message that the product is listening to
   * @param payments the payments of the ticket
   */
  public Order(
      long id,
      @Nullable User user,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull LinkedHashMap<String, Answer> details,
      @Nullable Freelancer freelancer,
      @Nullable OrderClaimingResponsiveMessage message,
      @NotNull List<String> payments) {
    super(id, user, status, channel, details, freelancer, payments);
    this.message = message;
  }

  /**
   * The constructor for when the ticket is just being created
   *
   * @param id the id of the ticket
   * @param customer the customer that created the ticket
   * @param channel the channel where the ticket was created
   */
  public Order(long id, @Nullable User customer, @Nullable TextChannel channel) {
    this(
        id,
        customer,
        TicketStatus.CREATING,
        channel,
        new LinkedHashMap<>(),
        null,
        null,
        new ArrayList<>());
  }

  /**
   * Sets the message that the order is listening to
   *
   * @param message the message that the order is listening to
   */
  public void setMessage(@NotNull Message message) {
    this.message = new OrderClaimingResponsiveMessage(message);
    refresh();
  }

  /**
   * Get the message that the order is listening to
   *
   * @return the message or null if there isn't one
   */
  @Nullable
  public OrderClaimingResponsiveMessage getMessage() {
    return message;
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.ORDER;
  }

  @Override
  public void addAnswer(@NotNull Question question, @NotNull Answer answer) {
    if (question.getSimple().equalsIgnoreCase("budget")
        && answer instanceof StringAnswer
        && Strings.containsIgnoreCase(((StringAnswer) answer).getAnswer(), "quote")) {
      try {
        Ticket ticket =
            TicketManager.getInstance().createTicket(TicketType.QUOTE, getMember(), this);
        if (ticket instanceof Quote) {
          ((Quote) ticket).addAnswer(question, answer);
        }
      } catch (DiscordManipulationException | TicketCreationException e) {
        if (channel != null) {
          Messages.error(e.getMessage()).send(channel);
        }
      }
    } else {
      super.addAnswer(question, answer);
    }
  }

  @Override
  public void onDone() {
    try {
      Messages.announce(this)
          .send(
              getType().getChannel(),
              msg -> {
                message = new OrderClaimingResponsiveMessage(msg);
              });
    } catch (DiscordManipulationException e) {
      Messages.error("This ticket could not be announced");
      Fallback.addError(e.getMessage());
    }
    super.onDone();
  }

  @Override
  public @NotNull Document getDocument() {
    Document document = super.getDocument();
    if (message != null) document.append("message", message);
    return document;
  }
}
