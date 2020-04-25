package com.starfishst.ethot.tickets.type;

import com.starfishst.ethot.config.objects.questions.Answer;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Apply extends QuestionsTicket {

  public Apply(
      long id,
      @Nullable User customer,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> answers) {
    super(id, customer, status, channel, answers);
  }

  public Apply(long id, @Nullable User customer, @Nullable TextChannel channel) {
    super(id, customer, TicketStatus.CREATING, channel, new HashMap<>());
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.APPLY;
  }
}
