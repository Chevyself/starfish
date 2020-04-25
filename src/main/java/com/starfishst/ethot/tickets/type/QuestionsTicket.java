package com.starfishst.ethot.tickets.type;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.objects.questions.Answer;
import com.starfishst.ethot.config.objects.questions.Question;
import com.starfishst.ethot.listeners.questions.QuestionTicketListener;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.util.Messages;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestionsTicket extends Ticket {

  @NotNull private final HashMap<String, Answer> answers;
  private int current = 0;

  protected QuestionsTicket(
      long id,
      @Nullable User customer,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> answers) {
    super(id, customer, status, channel);
    this.answers = answers;
  }

  public QuestionsTicket(long id, @Nullable User customer, @Nullable TextChannel channel) {
    this(id, customer, TicketStatus.CREATING, channel, new HashMap<>());
  }

  public QuestionsTicket(
      long id,
      @NotNull TicketStatus status,
      @Nullable User user,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> answers) {
    super(id, user, status, channel);
    this.answers = answers;
  }

  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    this.current = current;
  }

  public void addAnswer(@NotNull Question question, @NotNull Answer response) {
    answers.put(question.getSimple(), response);
    current++;
  }

  @Override
  public void onCreation() {
    if (channel != null) {
      Question question = Main.getConfiguration().getQuestions(this.getType()).get(0);
      QuestionTicketListener.sendNextMessage(
          this.id,
          channel,
          Messages.create(question.getTitle(), question.getDescription())
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

  @NotNull
  public HashMap<String, Answer> getAnswers() {
    return answers;
  }

  @Override
  public @NotNull Document getDocument() {
    return super.getDocument().append("details", answers);
  }
}
