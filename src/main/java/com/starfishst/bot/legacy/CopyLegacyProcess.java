package com.starfishst.bot.legacy;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.starfishst.bot.Main;
import com.starfishst.bot.exception.CopyProcessException;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.questions.Answer;
import com.starfishst.bot.objects.questions.RoleAnswer;
import com.starfishst.bot.objects.questions.StringAnswer;
import com.starfishst.bot.objects.responsive.type.orders.OrderClaimingResponsiveMessage;
import com.starfishst.bot.objects.responsive.type.product.ProductShopResponsiveMessage;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.loader.mongo.MongoTicketLoader;
import com.starfishst.bot.tickets.type.Apply;
import com.starfishst.bot.tickets.type.CheckOut;
import com.starfishst.bot.tickets.type.Order;
import com.starfishst.bot.tickets.type.Product;
import com.starfishst.bot.tickets.type.Quote;
import com.starfishst.bot.tickets.type.Support;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Console;
import com.starfishst.bot.util.Discord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a process that will copy the tickets and freelancers from Starfish 2.0.
 *
 * <p>Note that freelancers will have their ratings reset
 */
public class CopyLegacyProcess {

  /** The manager of tickets */
  @NotNull private final TicketManager manager;
  /** The name of the old database to copy tickets from */
  @NotNull private final String oldDatabase;
  /** The task that the process is running */
  @Nullable private CopyTask task;

  /**
   * Create an instance
   *
   * @param manager the ticket manager
   * @param oldDatabase the name of the old database
   */
  public CopyLegacyProcess(@NotNull TicketManager manager, @NotNull String oldDatabase) {
    this.manager = manager;
    this.oldDatabase = oldDatabase;
  }

  /**
   * Starts the process
   *
   * @throws CopyProcessException in case that the loader cannot be used
   */
  public void start() throws CopyProcessException {
    if (!(manager.getLoader() instanceof MongoTicketLoader))
      throw new CopyProcessException("Ticket loader is not " + MongoTicketLoader.class);
    task = (new CopyTask((MongoTicketLoader) manager.getLoader()));
    Console.info("Canceling other tasks");
    Main.getTasks().forEach(TimerTask::cancel);
    new Timer().schedule(task, 0);
  }

  /** The process when the task is finished */
  public void end() {
    if (task != null) {
      if (task.result == CopyLegacyProcessResult.RUNNING) {
        task.result = CopyLegacyProcessResult.ENDED;
      }
      if (task.resultMessage != null) {
        task.resultMessage = "Process ended successfully";
      }
      Console.info("Starting other tasks");
      Main.startTasks();
    }
  }

  /**
   * Get the ticket using the information provided by a document
   *
   * @param document the ticket document
   * @param id the id of the ticket
   * @param typeName the name of the type of ticket
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param customer the customer of the ticket
   * @param details the details of the ticket
   * @param freelancer the freelancer of the ticket
   * @return the ticket if it could be created else null
   */
  @Nullable
  private Ticket getTicket(
      Document document,
      long id,
      String typeName,
      TicketStatus status,
      TextChannel channel,
      User customer,
      HashMap<String, Answer> details,
      Freelancer freelancer) {
    Ticket ticket;
    if (typeName.equalsIgnoreCase("apply")) {
      ticket = new Apply(id, customer, status, channel, details, new ArrayList<>());
    } else if (typeName.equalsIgnoreCase("support")) {
      ticket = new Support(id, customer, status, channel, details, new ArrayList<>());
    } else if (typeName.equalsIgnoreCase("commission")) {
      OrderClaimingResponsiveMessage message = null;
      if (document.get("request") != null) {
        message = new OrderClaimingResponsiveMessage(document.getLong("request"));
      }
      ticket =
          new Order(id, customer, status, channel, details, freelancer, message, new ArrayList<>());
    } else if (typeName.equalsIgnoreCase("quote")) {
      ticket =
          new Quote(
              id,
              customer,
              status,
              channel,
              details,
              freelancer,
              new ArrayList<>(),
              new ArrayList<>());
    } else if (typeName.equalsIgnoreCase("package")) {
      ProductShopResponsiveMessage message = null;
      if (document.get("buy") != null) {
        message = new ProductShopResponsiveMessage(document.getLong("buy"));
      }
      ticket = new Product(id, customer, status, channel, details, message, new ArrayList<>());
    } else if (typeName.equalsIgnoreCase("checkout")) {
      if (document.getLong("product") != null) {
        ticket =
            new CheckOut(
                id,
                customer,
                status,
                channel,
                freelancer,
                document.getLong("product"),
                new ArrayList<>());
      } else {
        ticket =
            new CheckOut(
                id, customer, TicketStatus.CLOSED, channel, freelancer, -1, new ArrayList<>());
      }
    } else {
      Console.info(typeName + " is not a type anymore skipping");
      ticket = null;
    }
    return ticket;
  }

  /**
   * Get the freelancer of a ticket document
   *
   * @param document the ticket document
   * @return the freelancer if it exists else null
   */
  @Nullable
  private Freelancer getFreelancer(Document document) {
    if (document.get("freelancer") != null) {
      return manager.getLoader().getFreelancer(document.getLong("freelancer"));
    }
    return null;
  }

  /**
   * Get the details from a ticket document
   *
   * @param document the document to get the details from
   * @return the details
   */
  @NotNull
  private HashMap<String, Answer> getDetails(@NotNull Document document) {
    HashMap<String, Answer> details = new HashMap<>();
    if (document.get("details") != null) {
      Document detailsDocument = document.get("details", Document.class);
      detailsDocument.forEach(
          (string, object) -> {
            if (string.equalsIgnoreCase("roles")) {
              details.put(
                  "roles",
                  new RoleAnswer(
                      Discord.getRoles(
                          detailsDocument.getList(string, Long.class, new ArrayList<>()))));
            } else {
              if (object instanceof String) {
                details.put(string, new StringAnswer((String) object));
              } else {
                Console.info(string + " is not a valid key");
              }
            }
          });
    }
    return details;
  }

  /**
   * Get the customer from the document of a ticket
   *
   * @param document the document to get the customer from
   * @return the customer if found else null
   */
  @Nullable
  private User getCustomer(@NotNull Document document) {
    if (document.get("costumer") != null) {
      return Discord.getUser(document.getLong("costumer"));
    }
    return null;
  }

  /**
   * Get the task that is the process running
   *
   * @return the task
   */
  @Nullable
  public CopyTask getTask() {
    return task;
  }

  /** The task to run async */
  public class CopyTask extends TimerTask {

    /** The loader where the tickets where be taken from */
    @NotNull private final MongoTicketLoader loader;
    /** The result of the process */
    @NotNull private CopyLegacyProcessResult result = CopyLegacyProcessResult.RUNNING;
    /** A message sent by the result of the process */
    @Nullable private String resultMessage;

    /**
     * Start the task
     *
     * @param loader the loader that the task will use
     */
    private CopyTask(@NotNull MongoTicketLoader loader) {
      this.loader = loader;
    }

    /**
     * Copies the tickets from the old collection to the current {@link TicketManager#getLoader()}
     *
     * @param oldTickets the old tickets collection TODO make the method shorter
     */
    private void copyTickets(MongoCollection<Document> oldTickets) {
      Console.info("Starting to copy tickets");
      long total = oldTickets.countDocuments();
      long current = 0;
      Console.info("Total of documents to analyse " + total);
      MongoCursor<Document> ticketsCursor = oldTickets.find().cursor();
      while (ticketsCursor.hasNext()) {
        Console.info("Currently completed " + current + " out of " + total + " Tickets documents");
        current++;
        Document document = ticketsCursor.next();
        if (checkIfNull(document, "id", "type", "channel")) {
          continue;
        }
        long id = document.getLong("id");
        User customer = getCustomer(document);
        HashMap<String, Answer> details = getDetails(document);
        Freelancer freelancer = getFreelancer(document);
        TicketStatus status = TicketStatus.OPEN;
        TextChannel channel = Discord.getTextChannel(document.getLong("channel"));
        if (channel == null) {
          status = TicketStatus.CLOSED;
        }
        Ticket ticket =
            getTicket(
                document,
                id,
                document.getString("type"),
                status,
                channel,
                customer,
                details,
                freelancer);
        if (ticket != null) {
          manager.getLoader().saveTicket(ticket);
        }
      }
      Console.info("Tickets have been copied");
    }

    /**
     * Copies the tickets from t he old collection to the current {@link TicketManager#getLoader()}
     *
     * @param oldFreelancers the old freelancers collection
     */
    private void copyFreelancers(MongoCollection<Document> oldFreelancers) {
      Console.info("Starting to copy freelancers...");
      long total = oldFreelancers.countDocuments();
      long current = 0;
      MongoCursor<Document> freelancersCursor = oldFreelancers.find().cursor();
      while (freelancersCursor.hasNext()) {
        Console.info(
            "Currently completed " + current + " out of " + total + " Freelancer documents");
        current++;
        Document document = freelancersCursor.next();
        if (checkIfNull(document, "id", "experience")) {
          continue;
        }
        long id = document.getLong("id");
        List<String> portfolio = document.getList("experience", String.class);
        Member member;
        try {
          member = Discord.getMember(Discord.getUser(id));
        } catch (DiscordManipulationException e) {
          resultMessage = e.getMessage();
          result = CopyLegacyProcessResult.EXCEPTION;
          this.cancel();
          break;
        }
        if (member == null) {
          Console.info(document + " member is no longer in the server... Skipping");
          continue;
        }
        loader.saveFreelancer(new Freelancer(portfolio, new HashMap<>(), id));
      }
      Console.info("Finished copying freelancers");
    }

    /**
     * Checks if a document has certain keys
     *
     * @param document the document to check
     * @param keys the keys to find
     * @return true if one of the keys is null
     */
    private boolean checkIfNull(@NotNull Document document, @NotNull String... keys) {
      for (String key : keys) {
        if (document.get(key) == null) {
          Console.info(document + " does not have " + key + "... Skipping");
          return true;
        }
      }
      return false;
    }

    /**
     * Get the result of the process
     *
     * @return the result of the process
     */
    @NotNull
    public CopyLegacyProcessResult getResult() {
      return result;
    }

    /**
     * Get the result message of the process
     *
     * @return the result message of the process
     */
    @Nullable
    public String getResultMessage() {
      return resultMessage;
    }

    @Override
    public void run() {
      try {
        Console.info(
            "Starting copying process from database "
                + oldDatabase
                + " to "
                + loader.getDatabase().getName());
        Console.info("Checking " + oldDatabase);
        MongoDatabase oldRealDatabase = loader.getClient().getDatabase(oldDatabase);
        Console.info("Getting Freelancers and Tickets collections");
        MongoCollection<Document> oldFreelancers = oldRealDatabase.getCollection("Freelancers");
        MongoCollection<Document> oldTickets = oldRealDatabase.getCollection("Tickets");
        copyFreelancers(oldFreelancers);
        copyTickets(oldTickets);
      } catch (Exception e) {
        e.printStackTrace();
        result = CopyLegacyProcessResult.EXCEPTION;
        resultMessage = e.getMessage();
      }
      end();
    }
  }
}
