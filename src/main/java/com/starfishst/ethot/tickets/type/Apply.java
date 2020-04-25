package com.starfishst.ethot.tickets.type;

import com.starfishst.ethot.objects.questions.Answer;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/** This is the ticket used when a user wants to apply as a freelancer */
public class Apply extends QuestionsTicket {

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param customer the user that is applying
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param answers the answers given by the user
   */
  public Apply(
      long id,
      @Nullable User customer,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> answers) {
    super(id, customer, status, channel, answers);
  }

  /**
   * The constructor for when the ticket is just being created
   *
   * @param id the id of the ticket
   * @param customer the customer creating the ticket
   * @param channel the channel where the ticket was created
   */
  public Apply(long id, @Nullable User customer, @Nullable TextChannel channel) {
    super(id, customer, TicketStatus.CREATING, channel, new HashMap<>());
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.APPLY;
  }
}
