package com.starfishst.bot.tickets.type;

import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.objects.questions.Answer;
import com.starfishst.bot.objects.responsive.type.suggestions.SuggestionsResponsiveMessage;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import com.starfishst.core.fallback.Fallback;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** User for when the user wants to suggest something to the managers */
public class Suggestion extends QuestionsTicket {

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param customer the user that is applying
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param answers the answers given by the user
   * @param payments the payments of the ticket
   */
  public Suggestion(
      long id,
      @Nullable User customer,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull LinkedHashMap<String, Answer> answers,
      List<String> payments) {
    super(id, customer, status, channel, answers, payments);
  }

  /**
   * The constructor for when the ticket is just being created
   *
   * @param id the id of the ticket
   * @param customer the customer creating the ticket
   * @param channel the channel where the ticket was created
   */
  public Suggestion(long id, @Nullable User customer, @Nullable TextChannel channel) {
    super(id, customer, channel);
  }

  /** Calls the onDone super method */
  public void superOnDone() {
    try {
      Messages.announce(this).send(getType().getChannel());
    } catch (DiscordManipulationException e) {
      Messages.error("This ticket could not be announced");
      Fallback.addError(e.getMessage());
    }
    super.onDone();
  }

  @Override
  public void onDone() {
    if (channel != null) {
      HashMap<String, String> placeholders = Tickets.getPlaceholders(this);
      Messages.create(
              "SUGGESTION_CONFIRMATION_TITLE",
              "SUGGESTION_CONFIRMATION_DESCRIPTION",
              placeholders,
              placeholders)
          .send(
              channel,
              msg -> {
                new SuggestionsResponsiveMessage(msg, getId());
              });
    }
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.SUGGESTION;
  }
}
