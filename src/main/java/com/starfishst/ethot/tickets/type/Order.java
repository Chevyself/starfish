package com.starfishst.ethot.tickets.type;

import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.questions.Answer;
import com.starfishst.ethot.objects.questions.Question;
import com.starfishst.ethot.objects.questions.StringAnswer;
import com.starfishst.ethot.objects.responsive.type.orders.OrderClaimingResponsiveMessage;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Unicode;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

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
   */
  public Order(
      long id,
      @Nullable User user,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> details,
      @Nullable Freelancer freelancer,
      @Nullable OrderClaimingResponsiveMessage message) {
    super(id, user, status, channel, details, freelancer);
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
    this(id, customer, TicketStatus.CREATING, channel, new HashMap<>(), null, null);
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.ORDER;
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
  public void addAnswer(@NotNull Question question, @NotNull Answer answer) {
    if (question.getSimple().equalsIgnoreCase("budget")
        && answer instanceof StringAnswer
        && ((StringAnswer) answer).getAnswer().contains("quote")) {
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
                message = new OrderClaimingResponsiveMessage(msg.getIdLong());
                msg.addReaction(Unicode.WHITE_CHECK_MARK).queue();
              });
    } catch (DiscordManipulationException e) {
      Messages.error("This ticket could not be announced");
      Errors.addError(e.getMessage());
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
