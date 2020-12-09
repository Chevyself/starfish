package com.starfishst.bot.tickets;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.starfishst.api.Starfish;
import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.messages.BotResponsiveMessage;
import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.data.tickets.Offer;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.api.events.messages.BotMessageUnloadedEvent;
import com.starfishst.api.events.role.BotRoleUnloadedEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.api.events.user.FreelancerRatingUnloadedEvent;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.bot.data.StarfishFreelancerRating;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishRole;
import com.starfishst.bot.data.StarfishTicket;
import com.starfishst.bot.data.StarfishUser;
import com.starfishst.bot.data.messages.offer.OfferMessage;
import com.starfishst.bot.utility.EnumUtils;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import com.starfishst.jda.utils.responsive.controller.ResponsiveMessageController;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.Catchable;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class StarfishDataLoader implements DataLoader, ResponsiveMessageController {

  @NonNull private final Gson gson;

  @NonNull private final MongoClient client;

  @NonNull private final MongoDatabase database;

  @NonNull private final MongoCollection<Document> tickets;

  @NonNull private final MongoCollection<Document> users;

  @NonNull private final MongoCollection<Document> roles;

  @NonNull private final MongoCollection<Document> messages;

  @NonNull private final MongoCollection<Document> ratings;
  /**
   * Creates the mongo loader instance
   *
   * @param gson
   * @param uri the mongo uri for authentication
   * @param database the database to use
   */
  public StarfishDataLoader(@NonNull Gson gson, @NonNull String uri, @NonNull String database) {
    this.gson = gson;
    MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
    builder.connectTimeout(300).sslEnabled(true);
    this.client = new MongoClient(new MongoClientURI(uri, builder));
    this.database = this.client.getDatabase(database);
    this.tickets = this.database.getCollection("tickets");
    this.users = this.database.getCollection("users");
    this.roles = this.database.getCollection("roles");
    this.messages = this.database.getCollection("messages");
    this.ratings = this.database.getCollection("ratings");
    this.ping();
  }

  /**
   * Get an object first look in cache then in the mongo database
   *
   * @param clazz the class of the object to get
   * @param predicate to check the cache object
   * @param collection the collection that contains the type of object
   * @param query the mongo query to get the object from the database
   * @param <T> the type of object to get
   * @return the object
   */
  private <T extends Catchable> T getObject(
      @NonNull Class<T> clazz,
      @NonNull Predicate<T> predicate,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query) {
    return Starfish.getCache()
        .getOrSupply(clazz, predicate, this.supplyObjectFromQuery(clazz, collection, query));
  }

  /** Ping the mongo database to make sure that this is working */
  public void ping() {
    database.runCommand(new Document("serverStatus", 1));
  }

  @Override
  public boolean acceptBots() {
    return false;
  }

  /**
   * Just as {@link #getObjectFromQuery(Class, MongoCollection, Document)} but given as a supplier
   *
   * @param clazz the class of the object to get
   * @param collection the collection that contains that type of object
   * @param query the query to get the object from the collection
   * @param <T> the type of object to get
   * @return the supplier of the object
   */
  @NonNull
  private <T> Supplier<T> supplyObjectFromQuery(
      @NonNull Class<T> clazz,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query) {
    return () -> getObjectFromQuery(clazz, collection, query);
  }

  /**
   * Get an object from a query. This works for getting one object only
   *
   * @param typeOfT the type of object to supply
   * @param collection the collection to get the object from
   * @param query the query to match the object
   * @param <T> the type of the object
   * @return the supplier of the object
   */
  private <T> T getObjectFromQuery(
      @NotNull Class<T> typeOfT,
      @NotNull MongoCollection<Document> collection,
      @NotNull Document query) {
    Document first = collection.find(query).first();
    if (first != null) {
      T t = this.getObjectFromDocument(typeOfT, first);
      if (t instanceof StarfishCatchable) {
        ((StarfishCatchable) t).cache();
      }
      return t;
    } else {
      return null;
    }
  }

  /**
   * Saves an object into the mongo collection
   *
   * @param collection the mongo collection to save the document to
   * @param query the query to replace in case that the document already exists
   * @param object the object to save
   */
  private void save(
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @NonNull Object object) {
    Document document = Document.parse(this.gson.toJson(object));
    Document first = collection.find(query).first();
    if (first != null) {
      collection.replaceOne(query, document);
    } else {
      collection.insertOne(document);
    }
  }

  /**
   * Get an object given a document
   *
   * @param typeOfT the type of object to get from document
   * @param document the document to get the object from
   * @param <T> the type of the object
   * @return the object given by json
   */
  private <T> T getObjectFromDocument(@NotNull Type typeOfT, @NotNull Document document) {
    return this.gson.fromJson(document.toJson(), typeOfT);
  }

  /**
   * Get the objects from cache or search them in a collection
   *
   * @param <T> the type of the object
   * @param clazz the class of the object to get
   * @param collection the collection to get the objects from
   * @param query the query to match the objects
   * @return the list containing the object
   */
  @NonNull
  private <T extends Catchable> List<T> supplyManyAndCache(
      @NonNull Class<T> clazz,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @NonNull Predicate<T> predicate) {
    Cache cache = Starfish.getCache();
    List<T> list = this.supplyManyFromQuery(clazz, collection, query);
    List<T> toAdd = new ArrayList<>();
    list.removeIf(
        data -> {
          if (!cache.contains(data)) {
            cache.add(data);
          } else {
            T catchable = cache.get(clazz, data::equals);
            if (catchable != null) {
              if (predicate.test(catchable)) {
                toAdd.add(catchable);
              }
              return true;
            }
          }
          return false;
        });
    list.addAll(toAdd);
    return list;
  }

  /**
   * Supply all the objects matching the query. If the limit and skip are < 1 there will be no
   * elements skipped nor limited
   *
   * @param typeOfT the type of objects to supply
   * @param collection the collection to find the objects from
   * @param query the query to find the documents
   * @param <T> the type of objects to get
   * @return the list of objects
   */
  private <T> List<T> supplyManyFromQuery(
      @NonNull Type typeOfT,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query) {
    List<T> list = new ArrayList<>();
    MongoCursor<Document> cursor = collection.find(query).cursor();
    while (cursor.hasNext()) {
      T obj = this.getObjectFromDocument(typeOfT, cursor.next());
      if (obj != null) {
        list.add(obj);
      }
    }
    return list;
  }

  @Override
  public ResponsiveMessage getResponsiveMessage(Guild guild, long messageId) {
    return this.getObject(
        StarfishResponsiveMessage.class,
        message -> message.getId() == messageId,
        this.messages,
        new Document("id", messageId));
  }

  @Override
  public @NonNull Collection<ResponsiveMessage> getResponsiveMessages(Guild guild) {
    return new ArrayList<>();
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "mongo-database";
  }

  @Override
  public Ticket getTicket(long id) {
    return this.getObject(
        StarfishTicket.class, ticket -> ticket.getId() == id, this.tickets, new Document("id", id));
  }

  @Override
  public Ticket getTicketByChannel(long channelId) {
    return this.getObject(
        StarfishTicket.class,
        ticket -> ticket.getTextChannelId() == channelId,
        this.tickets,
        new Document("channel", channelId));
  }

  @Override
  public @NonNull BotUser getStarfishUser(long id) {
    return this.getObject(
        StarfishUser.class, user -> user.getId() == id, this.users, new Document("id", id));
  }

  @Override
  public @NonNull BotRole getStarfishRole(long id) {
    return this.getObject(
        StarfishRole.class, role -> role.getId() == id, this.roles, new Document("id", id));
  }

  @Override
  public @NonNull Collection<Offer> getOffers(@NonNull Ticket ticket) {
    return new ArrayList<>(
        this.supplyManyAndCache(
            OfferMessage.class,
            this.messages,
            new Document("data.ticket", ticket.getId()),
            offer -> offer.getTicket() == ticket.getId()));
  }

  @Override
  public void deleteMessage(@NonNull StarfishResponsiveMessage message) {
    message.unload(false);
    this.messages.deleteOne(new Document("id", message.getId()));
  }

  @Override
  public @NonNull FreelancerRating getRating(long id) {
    return this.getObject(
        StarfishFreelancerRating.class,
        rating -> rating.getId() == id,
        this.ratings,
        new Document("id", id));
  }

  @Override
  public @NonNull Collection<Ticket> getTickets(
      @NonNull BotUser user,
      @NonNull String role,
      @NonNull Collection<TicketType> types,
      TicketStatus... statuses) {
    Set<TicketType> typesToCheck =
        types.isEmpty() ? Lots.set(TicketType.values()) : new HashSet<>(types);
    Set<TicketStatus> statusSet =
        statuses.length == 0 ? Lots.set(TicketStatus.values()) : Lots.set(statuses);
    return new ArrayList<>(
        this.supplyManyAndCache(
            StarfishTicket.class,
            this.tickets,
            new Document("users." + user.getId(), role)
                .append("type", new Document("$in", EnumUtils.getNames(typesToCheck)))
                .append("status", new Document("$in", EnumUtils.getNames(statusSet))),
            ticket ->
                ticket.hasUser(user, role)
                    && typesToCheck.contains(ticket.getTicketType())
                    && statusSet.contains(ticket.getTicketStatus())));
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onRoleUnloadedEvent(@NonNull BotRoleUnloadedEvent event) {
    BotRole role = event.getRole();
    this.save(this.roles, new Document("id", role.getId()), role);
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onTicketUnloadedEvent(@NonNull TicketUnloadedEvent event) {
    Ticket ticket = event.getTicket();
    this.save(this.tickets, new Document("id", ticket.getId()), ticket);
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onFreelancerRatingUnloaded(@NonNull FreelancerRatingUnloadedEvent event) {
    FreelancerRating rating = event.getRating();
    this.save(this.ratings, new Document("id", rating.getId()), rating);
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onTicketStatusUpdated(@NonNull TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    this.save(this.tickets, new Document("id", ticket.getId()), ticket);
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onBotUserUnloaded(@NonNull BotUserUnloadedEvent event) {
    BotUser user = event.getUser();
    this.save(this.users, new Document("id", user.getId()), user);
  }

  @Override
  @Listener(priority = ListenPriority.HIGH)
  public void onBotMessageUnloaded(@NonNull BotMessageUnloadedEvent event) {
    BotResponsiveMessage message = event.getMessage();
    this.save(this.messages, new Document("id", message.getId()), message);
  }

  @SubscribeEvent
  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    ResponsiveMessageController.super.onMessageReactionAdd(event);
  }
}
