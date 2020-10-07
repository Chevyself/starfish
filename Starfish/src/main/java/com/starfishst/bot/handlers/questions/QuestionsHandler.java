package com.starfishst.bot.handlers.questions;

import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketAddDetailEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.console.Console;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.jda.result.ResultType;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import me.googas.commons.CoreFiles;
import me.googas.commons.Lots;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.gson.GsonProvider;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Handles questions tickets */
public class QuestionsHandler implements StarfishEventHandler {

  /** The map of the ticket and the current question it is in */
  @NotNull private final HashMap<Ticket, Integer> current = new HashMap<>();

  /** The loader to get users */
  @NotNull private final DataLoader loader;

  /** The questions for each type of ticket */
  @NotNull private final HashMap<TicketType, QuestionsConfiguration> questions;

  /**
   * Create the questions handler
   *
   * @param loader the loader to get users
   */
  public QuestionsHandler(@NotNull DataLoader loader) {
    this.loader = loader;
    this.questions = new HashMap<>();
    this.loadQuestions();
  }

  /** Loads the questions */
  private void loadQuestions() {
    List<TicketType> questionable =
        Lots.list(TicketType.APPLY, TicketType.ORDER, TicketType.PRODUCT, TicketType.SUPPORT);
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
        this.questions.put(type, GsonProvider.GSON.fromJson(reader, QuestionsConfiguration.class));
        this.questions.put(
            TicketType.QUOTE,
            this.questions.get(TicketType.ORDER)); // Copy the questions from orders
        reader.close();
        Console.info("'" + type + ".json' was loaded");
        // TODO print that questions were loaded
      } catch (IOException e) {
        // TODO add fallback
        e.printStackTrace();
      }
    }
  }

  /**
   * Listens for when a ticket is loaded to send the first question
   *
   * @param event the event of a ticket being loaded/created
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(@NotNull TicketStatusUpdatedEvent event) {
    if (!event.isCancelled() && event.getStatus() == TicketStatus.CREATING) {
      Ticket ticket = event.getTicket().refresh();
      TextChannel channel = ticket.getTextChannel();
      QuestionsConfiguration questions = this.questions.get(ticket.getTicketType());
      BotUser owner = event.getTicket().getOwner();
      if (!current.containsKey(ticket)
          && owner != null
          && questions != null
          && !questions.getQuestions().isEmpty()
          && channel != null) {
        Ticket parent = this.getTicketByChannel(channel);
        if (parent != null) {
          current.put(ticket, current.get(parent));
        } else {
          current.put(ticket, 0);
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
  public void onMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    if (!event.getAuthor().isBot()) {
      Ticket ticket = this.getTicketByChannel(event.getChannel());
      if (ticket != null) {
        BotUser user = loader.getStarfishUser(event.getAuthor().getIdLong());
        List<Question> questions = this.questions.get(ticket.getTicketType()).getQuestions();
        Question current = questions.get(this.current.get(ticket));
        Object answer = current.getAnswer(event, user);
        if (answer != null) {
          TicketAddDetailEvent addDetailEvent =
              new TicketAddDetailEvent(ticket, current.getSimple(), answer);
          if (!addDetailEvent.callAndGet()) {
            ticket = this.getTicketByChannel(event.getChannel());
            if (ticket != null) {
              ticket.getDetails().addValue(current.getSimple(), answer);
              this.sendNextQuestion(event, ticket, user, questions);
            } else {
              // TODO ?
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
      @NotNull GuildMessageReceivedEvent event,
      @NotNull Ticket ticket,
      @NotNull BotUser user,
      @NotNull List<Question> questions) {
    Question current;
    this.current.put(ticket, this.current.get(ticket) + 1);
    if (this.current.get(ticket) >= questions.size()) {
      ticket.refresh().setTicketStatus(TicketStatus.OPEN);
      this.current.remove(ticket);
    } else {
      current = questions.get(this.current.get(ticket.refresh()));
      current.getQuery(user).send(event.getChannel());
      if (current instanceof QuestionInformation) {
        sendNextQuestion(event, ticket, user, questions);
      }
    }
  }

  /**
   * Listens if a ticket is unloaded while being creating to remove it from the list
   *
   * @param event the event of a ticket being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketUnloaded(@NotNull TicketUnloadedEvent event) {
    Ticket ticket = event.getTicket();
    this.current.remove(ticket);
  }

  /**
   * Get a ticket using a channel
   *
   * @param channel the channel to get the ticket from
   * @return the ticket if found else null
   */
  @Nullable
  private Ticket getTicketByChannel(@NotNull TextChannel channel) {
    for (Ticket ticket : this.current.keySet()) {
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
