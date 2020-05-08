package com.starfishst.bot.tickets.loader.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.freelancers.Offer;
import com.starfishst.bot.objects.responsive.type.orders.OrderClaimingResponsiveMessage;
import com.starfishst.bot.objects.responsive.type.product.ProductShopResponsiveMessage;
import com.starfishst.bot.tickets.loader.TicketLoader;
import com.starfishst.bot.tickets.loader.mongo.codec.AnswerCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.FreelancerCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.ImageAnswerCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.OfferCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.OrderClaimingResponsiveMessageCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.ProductShopResponsiveMessageCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.ResponsiveMessageCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.ResponsiveMessageTypeCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.RoleAnswerCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.RoleCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.StringAnswerCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.TextChannelCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.TicketCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.TicketStatusCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.TicketTypeCodec;
import com.starfishst.bot.tickets.loader.mongo.codec.UserCodec;
import com.starfishst.bot.tickets.type.Order;
import com.starfishst.bot.tickets.type.Product;
import com.starfishst.bot.tickets.type.Quote;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Console;
import com.starfishst.bot.util.Tickets;
import com.starfishst.core.utils.Validate;
import com.starfishst.core.utils.cache.Cache;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.BsonReader;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This loads a ticket from a mongo database */
public class MongoTicketLoader implements TicketLoader {

  /** The instance of the loader for static use */
  @Nullable private static MongoTicketLoader instance;
  /** The mongo client created for the use of the database */
  @NotNull private final MongoClient client;
  /** The database that this will be using */
  @NotNull private final MongoDatabase database;
  /** The collection to work with tickets */
  @NotNull private final MongoCollection<Document> tickets;
  /** The collection to work with freelancers */
  @NotNull private final MongoCollection<Document> freelancers;

  /**
   * Creates the mongo loader instance
   *
   * @param uri the mongo uri for authentication
   * @param database the database to use
   */
  public MongoTicketLoader(@NotNull String uri, @NotNull String database) {
    MongoTicketLoader.instance = this;

    CodecRegistry codecRegistry = getCodecs();
    MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
    builder.connectTimeout(300).sslEnabled(true);
    builder.codecRegistry(codecRegistry);
    this.client = new MongoClient(new MongoClientURI(uri, builder));
    this.database = client.getDatabase(database);
    this.tickets = this.database.getCollection("tickets");
    this.freelancers = this.database.getCollection("freelancers");

    ping();
  }

  /** Ping the mongo database to make sure that this is working */
  public void ping() {
    database.runCommand(new Document("serverStatus", 1));
  }

  /**
   * Registers the codecs for the client to use and deserialize documents
   *
   * @return the codec registry for the client
   */
  @NotNull
  private CodecRegistry getCodecs() {
    return CodecRegistries.fromRegistries(
        CodecRegistries.fromCodecs(
            new AnswerCodec(),
            new FreelancerCodec(),
            new ImageAnswerCodec(),
            new OfferCodec(),
            new OrderClaimingResponsiveMessageCodec(),
            new ProductShopResponsiveMessageCodec(),
            new ResponsiveMessageCodec(),
            new ResponsiveMessageTypeCodec(),
            new RoleAnswerCodec(),
            new RoleCodec(),
            new StringAnswerCodec(),
            new TextChannelCodec(),
            new TicketCodec(),
            new TicketStatusCodec(),
            new TicketTypeCodec(),
            new UserCodec()),
        MongoClient.getDefaultCodecRegistry());
  }

  /**
   * Get the codec registry of the client
   *
   * @return the codec registry of the client
   */
  @NotNull
  public static CodecRegistry getCodecRegistry() {
    return getInstance().getClient().getMongoClientOptions().getCodecRegistry();
  }

  /**
   * Get the instance of mongo client
   *
   * @return the instance of mongo
   */
  @NotNull
  public static MongoTicketLoader getInstance() {
    return Validate.notNull(instance, "Mongo instance has not been initialized");
  }

  @Override
  public @Nullable Ticket getTicket(long id) {
    return (Ticket)
        Cache.getCache().stream()
            .filter(catchable -> catchable instanceof Ticket && ((Ticket) catchable).getId() == id)
            .findFirst()
            .orElseGet(() -> getTicketFromDatabase(id));
  }

  /**
   * Get the ticket from the collection 'tickets'
   *
   * @param id the id of the ticket
   * @return the ticket or null if not found
   */
  @Nullable
  private Ticket getTicketFromDatabase(long id) {
    Document document = getTicketDocument(id);
    if (document != null) {
      return getTicketFromDocument(document);
    } else {
      return null;
    }
  }

  /**
   * Gets a ticket using its document
   *
   * @param document the ticket document
   * @return the ticket built from the document
   */
  @NotNull
  private Ticket getTicketFromDocument(@NotNull Document document) {
    Codec<Ticket> codec = getCodecRegistry().get(Ticket.class);
    BsonReader reader = new JsonReader(document.toJson());
    return codec.decode(reader, DecoderContext.builder().build());
  }

  @Override
  public void saveTicket(Ticket ticket) {
    saveDocument(
        ticket.getId(),
        tickets,
        ticket.getDocument(),
        ticket + " has been saved into the database");
  }

  @Override
  public @Nullable Ticket getTicketByChannel(long id) {
    return (Ticket)
        Cache.getCache().stream()
            .filter(
                catchable -> {
                  if (catchable instanceof Ticket) {
                    TextChannel channel = ((Ticket) catchable).getChannel();
                    if (channel != null) {
                      return channel.getIdLong() == id;
                    }
                  }
                  return false;
                })
            .findFirst()
            .orElseGet(() -> getTicketByChannelFromDatabase(id));
  }

  /**
   * Get a ticket by its channel from the database
   *
   * @param id the id of the channel
   * @return the ticket if found
   */
  @Nullable
  private Ticket getTicketByChannelFromDatabase(long id) {
    Document query = new Document("channel", id);
    Document document = this.tickets.find(query).first();
    if (document != null) {
      return getTicketFromDocument(document);
    } else {
      return null;
    }
  }

  @Override
  public void saveFreelancer(Freelancer freelancer) {
    saveDocument(
        freelancer.getId(),
        freelancers,
        freelancer.getDocument(),
        freelancer + " has been saved into the database");
  }

  /**
   * Save a document into the database. The object must be able to be located using a Int64 id
   *
   * @param id the id of the object
   * @param collection the collection to save
   * @param document the document to save
   * @param message the string to say when it is saved
   */
  private void saveDocument(
      long id,
      @NotNull MongoCollection<Document> collection,
      @NotNull Document document,
      @NotNull String message) {
    Document query = new Document("id", id);
    Document result = collection.find(query).first();
    if (result != null) {
      collection.replaceOne(query, document);
    } else {
      collection.insertOne(document);
    }
    Console.info(message);
  }

  @Override
  public @NotNull List<Ticket> getTickets(@NotNull User user) {
    List<Ticket> tickets = new ArrayList<>();

    Cache.getCache()
        .forEach(
            catchable -> {
              if (catchable instanceof Ticket) {
                if (((Ticket) catchable).getUser() == user) {
                  tickets.add((Ticket) catchable);
                }
              }
            });

    tickets.addAll(getTicketsFromDatabase(user, Tickets.toIdList(tickets)));
    return tickets;
  }

  /**
   * Gets a list of tickets from the database matching the user
   *
   * @param user the user to match
   * @param loaded the already loaded tickets to skip
   * @return a list of brand new loaded tickets
   */
  @NotNull
  private List<Ticket> getTicketsFromDatabase(@NotNull User user, @NotNull List<Long> loaded) {
    List<Ticket> tickets = new ArrayList<>();
    Document query = new Document("user", user.getIdLong());
    this.tickets
        .find(query)
        .forEach(
            (Consumer<? super Document>)
                document -> {
                  if (document.get("id") != null) {
                    if (!loaded.contains(document.getLong("id"))) {
                      tickets.add(getTicketFromDocument(document));
                    }
                  }
                });
    return tickets;
  }

  @Override
  public @Nullable Order getOrderByMessage(long id) {
    return (Order)
        Cache.getCache().stream()
            .filter(
                catchable -> {
                  if (catchable instanceof Order) {
                    OrderClaimingResponsiveMessage message = ((Order) catchable).getMessage();
                    return message != null && message.getId() == id;
                  }
                  return false;
                })
            .findFirst()
            .orElseGet(() -> getOrderByMessageFromDatabase(id));
  }

  /**
   * Get an order from the database using the announce message id
   *
   * @param id the id of the message
   * @return the order if found
   */
  @Nullable
  private Order getOrderByMessageFromDatabase(long id) {
    Document query = new Document("message", new OrderClaimingResponsiveMessage(id));
    Document document = this.tickets.find(query).first();
    if (document != null) {
      return (Order) getTicketFromDocument(document);
    } else {
      return null;
    }
  }

  @Override
  public @Nullable Freelancer getFreelancer(long id) {
    return (Freelancer)
        Cache.getCache().stream()
            .filter(
                catchable ->
                    catchable instanceof Freelancer && ((Freelancer) catchable).getId() == id)
            .findFirst()
            .orElseGet(() -> getFreelancerFromDatabase(id));
  }

  @Override
  public @Nullable Quote getQuoteByOfferMessage(long id) {
    return (Quote)
        Cache.getCache().stream()
            .filter(
                catchable -> {
                  if (catchable instanceof Quote) {
                    for (Offer offer : ((Quote) catchable).getOffers()) {
                      if (offer.getMessage() != null && offer.getMessage().getId() == id) {
                        return true;
                      }
                    }
                  }
                  return false;
                })
            .findFirst()
            .orElseGet(() -> getQuoteByOfferMessageFromDatabase(id));
  }

  @Override
  public void close() {
    Console.info("Closing Mongo Client");
    client.close();
  }

  @Override
  public @Nullable Product getProductByMessage(long id) {
    return (Product)
        Cache.getCache().stream()
            .filter(
                catchable -> {
                  if (catchable instanceof Product) {
                    ProductShopResponsiveMessage message = ((Product) catchable).getMessage();
                    return message != null && message.getId() == id;
                  }
                  return false;
                })
            .findFirst()
            .orElseGet(() -> getProductByMessageFromDatabase(id));
  }

  @Override
  public void demoteFreelancer(Freelancer freelancer) {
    Cache.getCache().remove(freelancer);
    Document query = new Document("id", freelancer.getId());
    this.freelancers.deleteOne(query);
  }

  /**
   * Get a ticket from the database using its id. This is used in case it is not found in the cache
   *
   * @param paymentId the id of the payment
   * @return the ticket if found else null
   */
  @Nullable
  private Ticket getTicketByPaymentFromDatabase(String paymentId) {
    Document query = new Document("payments", paymentId);
    Document document = this.tickets.find(query).first();
    if (document != null) {
      return getTicketFromDocument(document);
    } else {
      return null;
    }
  }

  @Override
  public @Nullable Ticket getTicketByPayment(String paymentId) {
    return (Ticket)
        Cache.getCache().stream()
            .filter(
                catchable -> {
                  if (catchable instanceof Ticket) {
                    for (String payment : ((Ticket) catchable).getPayments()) {
                      if (payment.equals(paymentId)) {
                        return true;
                      }
                    }
                  }
                  return false;
                })
            .findFirst()
            .orElseGet(() -> getTicketByPaymentFromDatabase(paymentId));
  }

  /**
   * Get a product using its message from the tickets collection
   *
   * @param id the message id
   * @return the product if found else null
   */
  private Product getProductByMessageFromDatabase(long id) {
    Document query = new Document("message", new ProductShopResponsiveMessage(id));
    Document document = this.tickets.find(query).first();
    if (document != null) {
      return (Product) getTicketFromDocument(document);
    } else {
      return null;
    }
  }

  /**
   * Get a quote by its offer message from the database
   *
   * @param id the id of the offer message
   * @return the quote if found
   */
  @Nullable
  private Quote getQuoteByOfferMessageFromDatabase(long id) {
    Document query = new Document("offers.message", id);
    Document document = this.tickets.find(query).first();
    if (document != null) {
      return (Quote) getTicketFromDocument(document);
    } else {
      return null;
    }
  }

  /**
   * Get the freelancer from the database using its id
   *
   * @param id the id of the freelancer
   * @return the freelancer if found
   */
  @Nullable
  private Freelancer getFreelancerFromDatabase(long id) {
    Document query = new Document("id", id);
    Document document = this.freelancers.find(query).first();
    if (document != null) {
      return getFreelancerFromDocument(document);
    } else {
      return null;
    }
  }

  /**
   * Get a freelancer using its document
   *
   * @param document the freelancer document
   * @return the freelancer
   */
  @NotNull
  private Freelancer getFreelancerFromDocument(Document document) {
    Codec<Freelancer> codec = getCodecRegistry().get(Freelancer.class);
    BsonReader reader = new JsonReader(document.toJson());
    return codec.decode(reader, DecoderContext.builder().build());
  }

  /**
   * Get the document of a ticket using its id
   *
   * @param id the id of the ticket
   * @return the document if found
   */
  @Nullable
  public Document getTicketDocument(long id) {
    Document query = new Document("id", id);
    return tickets.find(query).first();
  }

  /**
   * Get the client used in the loader
   *
   * @return the client used in the loader
   */
  @NotNull
  public MongoClient getClient() {
    return client;
  }

  /**
   * Get the database that the loader is using
   *
   * @return the database that the loader is using
   */
  @NotNull
  public MongoDatabase getDatabase() {
    return database;
  }
}
