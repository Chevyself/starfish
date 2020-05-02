package com.starfishst.bot.tickets.type;

import com.starfishst.bot.objects.questions.Answer;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.TicketType;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A report can be submitted by an user towards another one */
public class Report extends QuestionsTicket {

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param customer the user that is applying
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param answers the answers given by the user
   */
  public Report(
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
  public Report(long id, @Nullable User customer, @Nullable TextChannel channel) {
    super(id, customer, channel);
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.REPORT;
  }
}
