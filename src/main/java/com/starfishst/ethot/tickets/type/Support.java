package com.starfishst.ethot.tickets.type;

import com.starfishst.ethot.config.objects.questions.Answer;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Support extends QuestionsTicket {

  public Support(
      long id,
      @Nullable User customer,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> answers) {
    super(id, customer, status, channel, answers);
  }

  public Support(long id, @Nullable User customer, @Nullable TextChannel channel) {
    super(id, customer, channel);
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.SUPPORT;
  }
}
