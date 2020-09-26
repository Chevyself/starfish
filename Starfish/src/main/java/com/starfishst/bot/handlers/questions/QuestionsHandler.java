package com.starfishst.bot.handlers.questions;

import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketLoadedEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import com.starfishst.utils.gson.GsonProvider;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
                "questions/" + type.toString().toLowerCase() + ".json");
        FileReader reader =
            new FileReader(file); //  This can't throw an exception for not finding the file
        questions.put(type, GsonProvider.GSON.fromJson(reader, QuestionsConfiguration.class));
        reader.close();
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
  public void onTicketLoadEvent(@NotNull TicketStatusUpdatedEvent event) {
    if (event.getStatus() == TicketStatus.CREATING) {
      Ticket ticket = event.getTicket();
      QuestionsConfiguration questions = this.questions.get(ticket.getTicketType());
      TextChannel channel = ticket.getTextChannel();
      List<BotUser> customers = event.getTicket().getUsers("customer");
      if (!customers.isEmpty()
              && questions != null
              && !questions.getQuestions().isEmpty()
              && channel != null) {
        current.put(ticket, 0);
        questions.getQuestions().get(0).getQuery(customers.get(0)).send(channel);
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
          ticket.getDetails().getPreferences().put(current.getSimple(), answer);
          this.current.put(ticket, this.current.get(ticket) + 1);
          if (this.current.get(ticket) >= questions.size()) {
            ticket.refresh().setTicketStatus(TicketStatus.OPEN);
            this.current.remove(ticket);
          } else {
            current = questions.get(this.current.get(ticket));
            current.getQuery(user).send(event.getChannel());
          }
        }
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
    if (this.current.containsKey(event.getTicket())
        && event.getTicket().getTicketStatus() == TicketStatus.CREATING) {
      this.current.remove(event.getTicket());
    }
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
