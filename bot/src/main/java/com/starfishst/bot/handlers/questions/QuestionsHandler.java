package com.starfishst.bot.handlers.questions;

import com.starfishst.api.Starfish;
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
import com.starfishst.bot.utility.Mongo;
import com.starfishst.jda.result.ResultType;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.CoreFiles;
import me.googas.commons.Lots;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
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
        Lots.list(
            TicketType.APPLY,
            TicketType.ORDER,
            TicketType.PRODUCT,
            TicketType.SUPPORT,
            TicketType.REPORT,
            TicketType.SUGGESTION);
    for (TicketType type : questionable) {
      try {
        File file =
            CoreFiles.getFileOrResource(
                CoreFiles.currentDirectory()
                    + "/assets/questions/"
                    + type.toString().toLowerCase()
                    + ".json",
                CoreFiles.getResource("questions/" + type.toString().toLowerCase() + ".json"));
        FileReader reader =
            new FileReader(file); //  This can't throw an exception for not finding the file
        this.questions.put(type, Mongo.GSON.fromJson(reader, QuestionsConfiguration.class));
        this.questions.put(
            TicketType.QUOTE,
            this.questions.get(TicketType.ORDER)); // Copy the questions from orders
        reader.close();
        // TODO print that questions were loaded
      } catch (IOException e) {
        Starfish.getFallback().process(e, "Questions for " + type + " could not be loaded");
      }
    }
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
      TextChannel channel = ticket.getTextChannel();
      QuestionsConfiguration questions = this.questions.get(ticket.getType());
      BotUser owner = event.getTicket().getOwner();
      if (!this.current.containsKey(ticket.getId())
          && owner != null
          && questions != null
          && !questions.getQuestions().isEmpty()
          && channel != null) {
        Ticket parent = this.getTicketByChannel(channel);
        if (parent != null) {
          this.current.put(ticket.getId(), this.current.get(parent.getId()));
        } else {
          this.current.put(ticket.getId(), 0);
          questions.getQuestions().get(0).getQuery(owner).send(channel);
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
      Ticket ticket = this.getTicketByChannel(event.getChannel());
      if (ticket != null) {
        BotUser user = Starfish.getLoader().getStarfishUser(event.getAuthor().getIdLong());
        List<Question> questions = this.questions.get(ticket.getType()).getQuestions();
        Question current = questions.get(this.current.get(ticket.getId()));
        Object answer = current.getAnswer(event, user);
        if (answer != null) {
          TicketAddDetailEvent addDetailEvent =
              new TicketAddDetailEvent(ticket, current.getSimple(), answer);
          if (!addDetailEvent.callAndGet()) {
            ticket = this.getTicketByChannel(event.getChannel());
            if (ticket != null) {
              ticket.getDetails().add(current.getSimple(), answer);
              this.sendNextQuestion(event, ticket, user, questions);
            }
          } else {
            Messages.build(addDetailEvent.getReason(), ResultType.ERROR, user);
          }
        }
      }
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
      current.getQuery(user).send(event.getChannel());
      if (current instanceof QuestionInformation) {
        this.sendNextQuestion(event, ticket, user, questions);
      }
    }
  }

  /** @see CleanerHandler#refresh(Ticket) */
  @NonNull
  private Ticket refresh(@NonNull Ticket ticket) {
    return Starfish.getHandler(CleanerHandler.class).refresh(ticket);
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
  @Nullable
  private Ticket getTicketByChannel(@NonNull TextChannel channel) {
    for (Long id : this.current.keySet()) {
      if (id == null) return null;
      Ticket ticket = Starfish.getLoader().getTicket(id);
      TextChannel textChannel = ticket.getTextChannel();
      if (textChannel != null && textChannel.equals(channel)) {
        return ticket;
      }
    }
    return null;
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
