package com.starfishst.ethot.tickets.type;

import com.starfishst.ethot.objects.questions.Answer;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** When a client needs help it needs to create this ticket */
public class Support extends QuestionsTicket {

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param customer the customer owner of the ticket
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param answers the answers provided by the ticket
   */
  public Support(
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
   * @param customer the customer that created the ticket
   * @param channel the channel where the ticket was created
   */
  public Support(long id, @Nullable User customer, @Nullable TextChannel channel) {
    super(id, customer, channel);
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.SUPPORT;
  }
}
