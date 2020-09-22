package com.starfishst.bot.oldtickets.type;

import com.starfishst.bot.oldconfig.questions.QuestionsHandler;
import com.starfishst.bot.listeners.questions.QuestionTicketListener;
import com.starfishst.bot.objects.questions.Answer;
import com.starfishst.bot.handlers.questions.Question;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.bot.util.Messages;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a type of ticket that will ask for details to provide to the freelancers, the management
 * team, etc.
 */
public class QuestionsTicket extends Ticket {

  /** The HashMap of the question simple and the answer */
  @NotNull private final LinkedHashMap<String, Answer> answers;
  /** The current question that the ticket is on */
  private int current = 0;

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param customer the customer owner of the ticket
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param answers the answers provided by the ticket
   * @param payments the payments of the ticket
   */
  protected QuestionsTicket(
      long id,
      @Nullable User customer,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull LinkedHashMap<String, Answer> answers,
      List<String> payments) {
    super(id, customer, status, channel, payments);
    this.answers = answers;
  }

  /**
   * The constructor for when the ticket is just being created
   *
   * @param id the id of the ticket
   * @param customer the customer that created the ticket
   * @param channel the channel where the ticket was created
   */
  public QuestionsTicket(long id, @Nullable User customer, @Nullable TextChannel channel) {
    this(id, customer, TicketStatus.CREATING, channel, new LinkedHashMap<>(), new ArrayList<>());
  }

  /**
   * Adds the answer to the questions and increases the current question
   *
   * @param question the questions answered (to get the simple from)
   * @param answer the answer gaven by the customer
   */
  public void addAnswer(@NotNull Question question, @NotNull Answer answer) {
    answers.put(question.getSimple(), answer);
    current++;
  }

  /**
   * Set the current question
   *
   * @param current the current question to set
   */
  public void setCurrent(int current) {
    this.current = current;
  }

  /**
   * Get the current question that the ticket is on
   *
   * @return the current question that the ticket is on
   */
  public int getCurrent() {
    return current;
  }

  /**
   * Get the map of answers/details
   *
   * @return the map of answers/details
   */
  @NotNull
  public LinkedHashMap<String, Answer> getAnswers() {
    return answers;
  }

  @Override
  public void onCreation() {
    if (channel != null) {
      Question question = QuestionsHandler.getInstance().getQuestions(this.getType()).get(0);
      QuestionTicketListener.sendNextMessage(
          this.id,
          channel,
          Messages.create(question.getBuiltTitle(), question.getBuiltDescription())
              .getAsMessageQuery()
              .getMessage(),
          null);
    }
  }

  @Override
  public @NotNull QuestionsTicket refresh() {
    return (QuestionsTicket) super.refresh();
  }

  @Override
  public void onDone() {
    if (channel != null) {
      QuestionTicketListener.sendNextMessage(
          this.id, this.channel, Messages.announce(this).getMessage(), msg -> msg.pin().queue());
      setStatus(TicketStatus.OPEN);
    }
  }

  @Override
  public @NotNull Document getDocument() {
    return super.getDocument().append("details", answers);
  }
}
