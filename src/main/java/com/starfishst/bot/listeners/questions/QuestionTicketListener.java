package com.starfishst.bot.listeners.questions;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.config.questions.QuestionsHandler;
import com.starfishst.bot.exception.TicketCreationException;
import com.starfishst.bot.objects.questions.Answer;
import com.starfishst.bot.objects.questions.Question;
import com.starfishst.bot.objects.questions.QuestionRole;
import com.starfishst.bot.objects.questions.RoleAnswer;
import com.starfishst.bot.objects.questions.StringAnswer;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.type.QuestionsTicket;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Messages;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.Lots;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Listens to {@link QuestionsTicket} when tye are being created and the answers of the user */
public class QuestionTicketListener {

  /** The active messages map: <Ticket Id, Message Id> */
  @NotNull private static final HashMap<Long, Long> active = new HashMap<>();
  /** The active messages sent by an user: <Ticket Id, Message Id> */
  @NotNull private static final HashMap<Long, Long> activeUserMessage = new HashMap<>();

  /**
   * Sends the next message/question to the channel and handles according to {@link
   * QuestionSendType}
   *
   * @param ticketId the ticket id asking questions
   * @param channel the channel receiving questions and answers
   * @param message the message to send
   * @param consumer in case something especial wants to be executed with the message
   */
  public static void sendNextMessage(
      long ticketId,
      @NotNull TextChannel channel,
      @NotNull Message message,
      @Nullable Consumer<Message> consumer) {
    switch (Configuration.getInstance().getQuestionSendType()) {
      case NONE:
        channel
            .sendMessage(message)
            .queue(
                msg -> {
                  active.put(ticketId, msg.getIdLong());
                  if (consumer != null) consumer.accept(msg);
                });
        break;
      case DELETE:
        if (active.containsKey(ticketId)) {
          channel.deleteMessageById(active.get(ticketId)).queue();
        }
        if (activeUserMessage.containsKey(ticketId)) {
          channel.deleteMessageById(activeUserMessage.get(ticketId)).queue();
        }
        channel
            .sendMessage(message)
            .queue(
                msg -> {
                  active.put(ticketId, msg.getIdLong());
                  if (consumer != null) consumer.accept(msg);
                });
        break;
      case REPLACE:
        if (active.containsKey(ticketId)) {
          channel
              .editMessageById(active.get(ticketId), message)
              .queue(
                  msg -> {
                    active.put(ticketId, msg.getIdLong());
                    if (consumer != null) consumer.accept(msg);
                  });
        }
        break;
    }
  }

  /**
   * When a message is received we should start the checks and set the answer.
   *
   * @param event the event of receiving a message inside a guild
   */
  @SubscribeEvent
  public void onMessageReceivedEvent(GuildMessageReceivedEvent event) {
    Ticket ticket =
        TicketManager.getInstance().getLoader().getTicketByChannel(event.getChannel().getIdLong());
    if (!event.getAuthor().isBot()
        && ticket instanceof QuestionsTicket
        && ticket.getStatus() == TicketStatus.CREATING) {
      QuestionsTicket questionsTicket = (QuestionsTicket) ticket;
      List<Question> questions = QuestionsHandler.getInstance().getQuestions(ticket.getType());
      Question question = questions.get(questionsTicket.getCurrent());
      Answer answer = getAnswer(event, question);
      activeUserMessage.put(questionsTicket.getId(), event.getMessage().getIdLong());
      if (answer != null) {
        questionsTicket.addAnswer(question, answer);

        // We reload the ticket in case that it changed (for example with quotes)
        questionsTicket =
            (QuestionsTicket)
                TicketManager.getInstance()
                    .getLoader()
                    .getTicketByChannel(event.getChannel().getIdLong());
        if (questionsTicket != null) {
          if (questionsTicket.getCurrent() >= questions.size()) {
            questionsTicket.refresh().onDone();
          } else {
            questionsTicket.refresh();
            question = questions.get(questionsTicket.getCurrent());
            sendNextMessage(
                questionsTicket.getId(),
                event.getChannel(),
                Messages.create(question.getBuiltTitle(), question.getBuiltDescription())
                    .getAsMessageQuery()
                    .getMessage(),
                null);
          }
        }
      } else {
        if (Configuration.getInstance().getQuestionSendType() == QuestionSendType.DELETE) {
          if (activeUserMessage.containsKey(questionsTicket.getId())) {
            event
                .getChannel()
                .deleteMessageById(activeUserMessage.get(questionsTicket.getId()))
                .queue();
          }
        }
      }
    }
  }

  /**
   * Get the answer of a question when all the checks have passed
   *
   * @param event the event of message received
   * @param question the question looking for answer
   * @return the answer for the question or null if the answer does not accomplish the parameters
   */
  @Nullable
  private Answer getAnswer(GuildMessageReceivedEvent event, Question question) {
    Message message = event.getMessage();
    List<Role> mentionedRoles = message.getMentionedRoles();
    TextChannel channel = event.getChannel();
    int limit = question.getLimit();
    if (question instanceof QuestionRole) {
      return getQuestionRoleAnswers((QuestionRole) question, mentionedRoles, channel, limit);
    } else {
      return getGenericQuestionAnswers(message, channel, limit);
    }
  }

  /**
   * Get the answer to a generic question
   *
   * @param message the message to get the answer from
   * @param channel the channel where the answer was sent
   * @param limit the limit of characters
   * @return the answer for the question or null if the answer does not accomplish the parameters
   */
  @Nullable
  private Answer getGenericQuestionAnswers(Message message, TextChannel channel, int limit) {
    if (message.getContentRaw().length() < limit) {
      return new StringAnswer(message.getContentRaw());
    } else {
      HashMap<String, String> placeHolders = new HashMap<>();
      placeHolders.put("limit", String.valueOf(limit));
      Messages.error(Lang.get("TOO_LONG_ANSWER", placeHolders))
          .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
      return null;
    }
  }

  /**
   * Get the answer to a role question
   *
   * @param question the question needing for an answer
   * @param mentionedRoles the mentioned roles in the discord message
   * @param channel the channel where the answer was sent
   * @param limit the limit of roles
   * @return the answer for the question or null if the answer does not accomplish the parameters
   */
  @Nullable
  private RoleAnswer getQuestionRoleAnswers(
      QuestionRole question, List<Role> mentionedRoles, TextChannel channel, int limit) {
    if (mentionedRoles.isEmpty()) {
      Messages.error(Lang.get("MENTION_ROLE"))
          .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
      return null;
    } else if (mentionedRoles.size() > limit) {
      HashMap<String, String> placeHolders = new HashMap<>();
      placeHolders.put("limit", String.valueOf(limit));
      Messages.error(Lang.get("ROLE_LIMIT", placeHolders))
          .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
      return null;
    }
    try {
      List<Role> notMentionable = getNotMentionableRoles(mentionedRoles, question.getRoles());
      if (!notMentionable.isEmpty()) {
        HashMap<String, String> placeHolders = new HashMap<>();
        placeHolders.put("roles", Lots.pretty(notMentionable));
        Messages.error(Lang.get("NOT_MENTIONABLE", placeHolders))
            .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
        return null;
      } else {
        return new RoleAnswer(mentionedRoles);
      }
    } catch (TicketCreationException e) {
      Fallback.addError(e.getMessage());
      Messages.error(e.getMessage())
          .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
      return null;
    }
  }

  /**
   * This checks that the list 'toCheck' does not contain any tickets outside of 'roles'
   *
   * @param toCheck the list to check
   * @param roles the only roles that can be in the list to check
   * @return a list of the roles mentioned that cannot be
   */
  @NotNull
  private List<Role> getNotMentionableRoles(@NotNull List<Role> toCheck, List<Role> roles) {
    List<Role> notMentionable = new ArrayList<>();
    toCheck.forEach(
        role -> {
          if (!roles.contains(role)) {
            notMentionable.add(role);
          }
        });
    return notMentionable;
  }

  /**
   * Sets an active in the hash map
   *
   * @param ticketId the ticket id
   * @param messageId the message id
   */
  public static void put(long ticketId, long messageId) {
    active.put(ticketId, messageId);
  }
}
