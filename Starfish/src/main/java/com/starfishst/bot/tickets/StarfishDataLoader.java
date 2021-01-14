package com.starfishst.bot.tickets;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.starfishst.api.events.messages.BotMessageUnloadedEvent;
import com.starfishst.api.events.role.BotRoleUnloadedEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.api.events.user.FreelancerRatingUnloadedEvent;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.role.BotRole;
import com.starfishst.api.tickets.Offer;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.user.FreelancerRating;
import com.starfishst.bot.data.StarfishFreelancerRating;
import com.starfishst.bot.data.StarfishRole;
import com.starfishst.bot.data.StarfishTicket;
import com.starfishst.bot.data.StarfishUser;
import com.starfishst.bot.utility.Enums;
import com.starfishst.bot.utility.Mongo;
import com.starfishst.bot.utility.Offers;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import com.starfishst.jda.utils.responsive.controller.ResponsiveMessageController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.bson.Document;

public class StarfishDataLoader implements Loader, ResponsiveMessageController {

  @NonNull @Getter private final MongoClient client;
  @NonNull @Getter private final MongoDatabase database;
  /**
   * Creates the mongo loader instance
   *
   * @param uri the mongo uri for authentication
   * @param database the database to use
   */
  public StarfishDataLoader(@NonNull String uri, @NonNull String database) {
    MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
    builder.connectTimeout(300).sslEnabled(true);
    this.client = new MongoClient(new MongoClientURI(uri, builder));
    this.database = this.client.getDatabase(database);
    this.ping();
  }
  /** Ping the mongo database to make sure that this is working */
  public void ping() {
    database.runCommand(new Document("serverStatus", 1));
  }

  @Override
  public boolean acceptBots() {
    return false;
  }

  @NonNull
  public MongoCollection<Document> messages() {
    return this.database.getCollection("messages");
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

  @NonNull
  public MongoCollection<Document> tickets() {
    return this.database.getCollection("tickets");
  }

  @NonNull
  public MongoCollection<Document> users() {
    return this.database.getCollection("users");
  }

  @NonNull
  public MongoCollection<Document> roles() {
    return this.database.getCollection("roles");
  }

  @NonNull
  public MongoCollection<Document> ratings() {
    return this.database.getCollection("ratings");
  }

  @Override
  public ResponsiveMessage getResponsiveMessage(Guild guild, long messageId) {
    return Mongo.get(
        BotResponsiveMessage.class,
        messages(),
        new Document("id", messageId),
        message -> message.getId() == messageId);
  }

  @Override
  public Ticket getTicket(long id) {
    return Mongo.get(
        StarfishTicket.class, tickets(), new Document("id", id), ticket -> ticket.getId() == id);
  }

  @Override
  public Ticket getTicketByChannel(long channelId) {
    return Mongo.get(
        StarfishTicket.class,
        tickets(),
        new Document("channel", channelId),
        ticket -> ticket.getTextChannelId() == channelId);
  }

  @Override
  public @NonNull BotUser getStarfishUser(long id) {
    StarfishUser starfishUser =
        Mongo.get(StarfishUser.class, users(), new Document("id", id), user -> user.getId() == id);
    if (starfishUser != null) return starfishUser;
    return new StarfishUser(id).cache();
  }

  @Override
  public @NonNull BotRole getStarfishRole(long id) {
    StarfishRole starfishRole =
        Mongo.get(StarfishRole.class, roles(), new Document("id", id), role -> role.getId() == id);
    if (starfishRole != null) return starfishRole;
    return new StarfishRole(id, new HashSet<>()).cache();
  }

  @Override
  public @NonNull Collection<Offer> getOffers(@NonNull Ticket ticket) {
    return Offers.fromMessages(
        Mongo.getMany(
            BotResponsiveMessage.class,
            messages(),
            new Document("data.ticket", ticket.getId()),
            null,
            -1,
            -1));
  }

  @Override
  public void deleteMessage(@NonNull BotResponsiveMessage message) {
    message.unload(false);
    messages().deleteOne(new Document("id", message.getId()));
  }

  @Override
  public @NonNull FreelancerRating getRating(long id) {
    StarfishFreelancerRating freelancerRating =
        Mongo.get(
            StarfishFreelancerRating.class,
            ratings(),
            new Document("id", id),
            rating -> rating.getId() == id);
    if (freelancerRating != null) return freelancerRating;
    return new StarfishFreelancerRating(id, new HashMap<>());
  }

  @Override
  public @NonNull Collection<Ticket> getTickets(
      @NonNull BotUser user,
      @NonNull String role,
      @NonNull Collection<TicketType> types,
      TicketStatus... statuses) {
    Set<TicketType> typesSet =
        types.isEmpty() ? Lots.set(TicketType.values()) : new HashSet<>(types);
    Set<TicketStatus> statusSet =
        statuses.length == 0 ? Lots.set(TicketStatus.values()) : Lots.set(statuses);
    Document query =
        new Document("users." + user.getId(), role)
            .append("type", new Document("$in", Enums.getNames(statusSet)))
            .append("status", new Document("$in", Enums.getNames(statusSet)));
    return new ArrayList<>(
        Mongo.getMany(
            StarfishTicket.class,
            tickets(),
            query,
            null,
            -1,
            -1,
            ticket ->
                ticket.hasUser(user, role)
                    && typesSet.contains(ticket.getType())
                    && statusSet.contains(ticket.getStatus())));
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onRoleUnloadedEvent(@NonNull BotRoleUnloadedEvent event) {
    BotRole role = event.getRole();
    Mongo.save(roles(), new Document("id", role.getId()), role);
  }

  @SubscribeEvent
  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    ResponsiveMessageController.super.onMessageReactionAdd(event);
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onTicketUnloadedEvent(@NonNull TicketUnloadedEvent event) {
    Ticket ticket = event.getTicket();
    Mongo.save(tickets(), new Document("id", ticket.getId()), ticket);
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onFreelancerRatingUnloaded(@NonNull FreelancerRatingUnloadedEvent event) {
    FreelancerRating rating = event.getRating();
    Mongo.save(ratings(), new Document("id", rating.getId()), rating);
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onTicketStatusUpdated(@NonNull TicketStatusUpdatedEvent event) {
    Ticket ticket = event.getTicket();
    Mongo.save(tickets(), new Document("id", ticket.getId()), ticket);
  }

  @Listener(priority = ListenPriority.HIGH)
  @Override
  public void onBotUserUnloaded(@NonNull BotUserUnloadedEvent event) {
    BotUser user = event.getUser();
    Mongo.save(users(), new Document("id", user.getId()), user);
  }

  @Override
  @Listener(priority = ListenPriority.HIGH)
  public void onBotMessageUnloaded(@NonNull BotMessageUnloadedEvent event) {
    BotResponsiveMessage message = event.getMessage();
    Mongo.save(messages(), new Document("id", message.getId()), message);
  }
}
