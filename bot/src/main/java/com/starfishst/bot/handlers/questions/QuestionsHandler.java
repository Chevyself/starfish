package com.starfishst.bot.handlers.questions;

import com.starfishst.api.Starfish;
import com.starfishst.api.StarfishFiles;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketAddDetailEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.handlers.misc.CleanerHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import me.googas.commands.jda.result.ResultType;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** Handles questions tickets */
public class QuestionsHandler implements StarfishHandler {

  /** The map of the ticket and the current question it is in */
  @NonNull private final HashMap<Long, Integer> current = new HashMap<>();

  /** The questions for each type of ticket */
  @NonNull private final HashMap<TicketType, QuestionsConfiguration> questions;

  /** Create the questions handler */
  public QuestionsHandler() {
    this.questions = new HashMap<>();
  }

  /** Loads the questions */
  private void loadQuestions() {
    List<TicketType> questionable =
        Arrays.asList(
            TicketType.APPLY,
            TicketType.ORDER,
            TicketType.PRODUCT,
            TicketType.SUPPORT,
            TicketType.REPORT,
            TicketType.SUGGESTION);
    for (TicketType type : questionable) {
      QuestionsConfiguration configuration =
          StarfishFiles.Assets.Questions.getQuestionsFile(type)
              .readOr(
                  Starfish.getJson(),
                  QuestionsConfiguration.class,
                  StarfishFiles.Resources.getQuestionResource(type));
      this.questions.put(type, configuration);
    }
    this.questions.put(
        TicketType.QUOTE, this.questions.get(TicketType.ORDER)); // Copy the questions from orders
  }

  @Override
  public void onEnable() {
    this.loadQuestions();
  }

  /**
   * Listens for when a ticket is loaded to send the first question
   *
   * @param event the event of a ticket being loaded/created
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(@NonNull TicketStatusUpdatedEvent event) {
    if (!event.isCancelled() && event.getStatus() == TicketStatus.CREATING) {
      Ticket ticket = this.refresh(event.getTicket());
      Optional<TextChannel> optionalChannel = ticket.getTextChannel();
      QuestionsConfiguration questions = this.questions.get(ticket.getType());
      Optional<BotUser> optionalOwner = event.getTicket().getOwner();
      if (!this.current.containsKey(ticket.getId())
          && optionalOwner.isPresent()
          && questions != null
          && !questions.getQuestions().isEmpty()
          && optionalChannel.isPresent()) {
        TextChannel channel = optionalChannel.get();
        BotUser owner = optionalOwner.get();
        Ticket parent = this.getTicketByChannel(channel).orElse(null);
        if (parent != null) {
          this.current.put(ticket.getId(), this.current.get(parent.getId()));
        } else {
          this.current.put(ticket.getId(), 0);
          channel
              .sendMessageEmbeds(questions.getQuestions().get(0).getQuery(owner).build())
              .queue();
        }
      }
    }
  }

  /**
   * Adds the detail to the ticket when a message is received
   *
   * @param event the event of receiving a message
   */
  @SubscribeEvent
  public void onMessageReceived(@NonNull GuildMessageReceivedEvent event) {
    if (!event.getAuthor().isBot()) {
      this.getTicketByChannel(event.getChannel())
          .ifPresent(
              ticket -> {
                BotUser user = Starfish.getLoader().getStarfishUser(event.getAuthor().getIdLong());
                List<Question> questions = this.questions.get(ticket.getType()).getQuestions();
                Question current = questions.get(this.current.get(ticket.getId()));
                Object answer = current.getAnswer(event, user);
                if (answer != null) {
                  TicketAddDetailEvent addDetailEvent =
                      new TicketAddDetailEvent(ticket, current.getSimple(), answer);
                  if (!addDetailEvent.callAndGet()) {
                    this.getTicketByChannel(event.getChannel())
                        .ifPresent(
                            next -> {
                              next.getDetails().add(current.getSimple(), answer);
                              this.sendNextQuestion(event, ticket, user, questions);
                            });
                  } else {
                    Messages.build(addDetailEvent.getReason(), ResultType.ERROR, user);
                    // ?
                  }
                }
              });
    }
  }

  /**
   * Send the next question
   *
   * @param event the event of the received message
   * @param ticket the ticket that is getting the questions
   * @param user the user that is answering the questions
   * @param questions the questions that are being asked to the ticket
   */
  private void sendNextQuestion(
      @NonNull GuildMessageReceivedEvent event,
      @NonNull Ticket ticket,
      @NonNull BotUser user,
      @NonNull List<Question> questions) {
    Question current;
    this.current.put(ticket.getId(), this.current.get(ticket.getId()) + 1);
    if (this.current.get(ticket.getId()) >= questions.size()) {
      this.refresh(ticket).setStatus(TicketStatus.OPEN);
      this.current.remove(ticket.getId());
    } else {
      current = questions.get(this.current.get(this.refresh(ticket).getId()));
      event.getChannel().sendMessageEmbeds(current.getQuery(user).build()).queue();
      if (current instanceof QuestionInformation) {
        this.sendNextQuestion(event, ticket, user, questions);
      }
    }
  }

  /** @see CleanerHandler#refresh(Ticket) */
  @NonNull
  private Ticket refresh(@NonNull Ticket ticket) {
    return Starfish.requireHandler(CleanerHandler.class).refresh(ticket);
  }

  /**
   * Listens if a ticket is unloaded while being creating to remove it from the list
   *
   * @param event the event of a ticket being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketUnloaded(@NonNull TicketUnloadedEvent event) {
    Ticket ticket = event.getTicket();
    this.current.remove(ticket.getId());
  }

  /**
   * Get a ticket using a channel
   *
   * @param channel the channel to get the ticket from
   * @return the ticket if found else null
   */
  @NonNull
  private Optional<? extends Ticket> getTicketByChannel(@NonNull TextChannel channel) {
    return this.current.keySet().stream()
        .map(id -> Starfish.getLoader().getTicket(id))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(
            ticket -> {
              Optional<TextChannel> textChannel = ticket.getTextChannel();
              return textChannel.isPresent() && textChannel.get().equals(channel);
            })
        .findFirst();
  }

  @Override
  public String getName() {
    return "questions";
  }

  @Override
  public void onUnload() {
    this.questions.clear();
  }
}
