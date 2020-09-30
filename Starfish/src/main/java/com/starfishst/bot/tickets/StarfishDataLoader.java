package com.starfishst.bot.tickets;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.starfishst.api.Permissible;
import com.starfishst.api.Permission;
import com.starfishst.api.PermissionStack;
import com.starfishst.api.configuration.MongoConfiguration;
import com.starfishst.api.data.messages.BotResponsiveMessage;
import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.messages.BotMessageUnloadedEvent;
import com.starfishst.api.events.role.BotRoleUnloadedEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.bot.data.StarfishFreelancer;
import com.starfishst.bot.data.StarfishPermission;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishRole;
import com.starfishst.bot.data.StarfishUser;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.tickets.deserializers.ClaimOrderDeserializer;
import com.starfishst.bot.tickets.deserializers.OfferDeserializer;
import com.starfishst.bot.tickets.deserializers.TicketPanelDeserializer;
import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for the data loader */
public class StarfishDataLoader implements StarfishLoader {

  /** The jda instance to get important information of discord */
  @NotNull private final JDA jda;
  /** The mongo client created for the use of the database */
  @NotNull private final MongoClient client;
  /** The database that is being used */
  @NotNull private final MongoDatabase database;
  /** The collection to work with tickets */
  @NotNull private final MongoCollection<Document> tickets;
  /** The collection to work with freelancers */
  @NotNull private final MongoCollection<Document> users;
  /** The collection to work with freelancers */
  @NotNull private final MongoCollection<Document> roles;
  /** The collection to work with responsive messages */
  @NotNull private final MongoCollection<Document> messages;
  /** The deserializers to get the responsive messages */
  @NotNull
  private final HashMap<String, StarfishMessageDeserializer<?>> deserializers = new HashMap<>();

  /**
   * Creates the mongo loader instance
   *
   * @param jda the jda instance for important discord information
   * @param uri the mongo uri for authentication
   * @param database the database to use
   */
  public StarfishDataLoader(@NotNull JDA jda, @NotNull String uri, @NotNull String database) {
    this.jda = jda;
    MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
    builder.connectTimeout(300).sslEnabled(true);
    this.client = new MongoClient(new MongoClientURI(uri, builder));
    this.database = client.getDatabase(database);
    this.tickets = this.database.getCollection("tickets");
    this.users = this.database.getCollection("users");
    this.roles = this.database.getCollection("roles");
    this.messages = this.database.getCollection("messages");
    this.ping();
    deserializers.put("claim-order", new ClaimOrderDeserializer());
    deserializers.put("ticket-panel", new TicketPanelDeserializer());
    deserializers.put("offer", new OfferDeserializer());
  }

  /**
   * Creates the mongo loader instance
   *
   * @param jda the jda instance for important discord information
   * @param configuration the configuration to get the uri and database from
   */
  public StarfishDataLoader(@NotNull JDA jda, @NotNull MongoConfiguration configuration) {
    this(jda, configuration.getUri(), configuration.getDatabase());
  }

  /** Ping the mongo database to make sure that this is working */
  public void ping() {
    database.runCommand(new Document("serverStatus", 1));
  }

  /**
   * Get an starfish user from a document query
   *
   * @param query the document query
   * @return the starfish user
   */
  @Nullable
  public BotUser getStarfishUser(@NotNull Document query) {
    Document first = this.users.find(query).first();
    if (first != null) {
      StarfishValuesMap preferences = this.getPreferences(first);
      if (preferences.getValueOr("freelancer", Boolean.class, false)) {
        return new StarfishFreelancer(
            query.getLong("id"), preferences, this.getPermissionStacks(first));
      } else {
        return new StarfishUser(query.getLong("id"), preferences, this.getPermissionStacks(first));
      }
    }
    return null;
  }

  /**
   * Get a ticket from a query
   *
   * @param query the query of the ticket
   * @return the ticket if found else null
   */
  @Nullable
  public Ticket getTicket(@NotNull Document query) {
    Document first = this.tickets.find(query).first();
    HashMap<BotUser, String> usersMap = new HashMap<>();
    if (first != null) {
      if (first.get("users") != null) {
        Document users = first.get("users", Document.class);
        for (String key : users.keySet()) {
          BotUser user = this.getStarfishUser(Long.parseLong(key));
          usersMap.put(user, users.getString(key));
        }
      }
      // TODO add freelancers and owner
      return new StarfishTicket(
          first.getLong("id"),
          TicketType.valueOf(first.getString("type").toUpperCase()),
          this.getDetails(first),
          TicketStatus.valueOf(first.getString("status").toUpperCase()),
          usersMap,
          jda.getTextChannelById(first.get("channel") != null ? first.getLong("channel") : -1));
    }
    return null;
  }

  /**
   * Get a starfish role from the database for certain query
   *
   * @param query the query to find the role
   * @return the role if found else null
   */
  public @Nullable BotRole getStarfishRole(@NotNull Document query) {
    Document first = this.roles.find(query).first();
    if (first != null) {
      return new StarfishRole(first.getLong("id"), this.getPermissionStacks(first));
    }
    return null;
  }

  /**
   * Get the permission stack from a document
   *
   * @param document the document to get the stack from
   * @return the set of the permission stacks
   */
  @NotNull
  private Set<PermissionStack> getPermissionStacks(Document document) {
    Set<PermissionStack> permissions = new HashSet<>();
    if (document.get("permissions") instanceof List) {
      for (Document stackDocument : document.getList("permissions", Document.class)) {
        Set<Permission> guidoPermissions = new HashSet<>();
        List<String> stack = stackDocument.getList("permissions", String.class);
        for (String string : stack) {
          if (string.startsWith("-")) {
            guidoPermissions.add(new StarfishPermission(string.substring(1), false));
          } else {
            guidoPermissions.add(new StarfishPermission(string, true));
          }
        }
        permissions.add(
            new StarfishPermissionStack(stackDocument.getString("context"), guidoPermissions));
      }
    }
    return permissions;
  }

  /**
   * Get the map of preferences from a document
   *
   * @param document the document to get the map
   * @return the map of links
   */
  @NotNull
  private StarfishValuesMap getPreferences(@NotNull Document document) {
    return this.getPreferences(document, "preferences");
  }
  /**
   * Get the map of preferences from a document
   *
   * @param document the document to get the map
   * @param key the key of to get in the document
   * @return the map of links
   */
  @NotNull
  private StarfishValuesMap getPreferences(@NotNull Document document, @NotNull String key) {
    HashMap<String, Object> preferences = new HashMap<>();
    if (document.get(key) instanceof Document) {
      document.get(key, Document.class).forEach((preferences::put));
    }
    return new StarfishValuesMap(preferences);
  }

  /**
   * Get ticket details from a document
   *
   * @param document the document to get ticket details
   * @return the details
   */
  @NotNull
  private StarfishTicketDetails getDetails(@NotNull Document document) {
    LinkedHashMap<String, Object> details = new LinkedHashMap<>();
    if (document.get("details") instanceof Document) {
      document
          .get("details", Document.class)
          .forEach(
              (string, object) -> {
                if (string.startsWith("role")) {
                  List<Role> roles = new ArrayList<>();
                  List<Long> ids = ((Document) document.get("details")).getList(string, Long.class);
                  for (Long id : ids) {
                    Role role = this.jda.getRoleById(id);
                    if (role != null) {
                      roles.add(role);
                    }
                  }
                  details.put(string, roles);
                } else {
                  details.put(string, object);
                }
              });
    }
    return new StarfishTicketDetails(details);
  }

  /**
   * Listen to a role being unloaded to save it to the database
   *
   * @param event the event of a role being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onRoleUnloadedEvent(@NotNull BotRoleUnloadedEvent event) {
    BotRole role = event.getRole();
    Document document =
        new Document("id", role.getId())
            .append("permissions", this.getPermissionStacksDocument(role));
    Document query = new Document("id", role.getId());
    Document first = this.roles.find(query).first();
    if (first != null) {
      this.roles.replaceOne(query, document);
    } else {
      this.roles.insertOne(document);
    }
  }

  /**
   * Listens to when a ticket is unloaded
   *
   * @param event the event of a ticket being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketUnloadedEvent(@NotNull TicketUnloadedEvent event) {
    saveTicket(event.getTicket());
  }

  /**
   * Saves a ticket into the mongo database
   *
   * @param ticket the ticket to save
   */
  private void saveTicket(@NotNull Ticket ticket) {
    Document document =
        new Document("id", ticket.getId())
            .append("type", ticket.getTicketType().toString())
            .append("users", this.usersToDocument(ticket))
            .append("details", this.detailsToDocument(ticket))
            .append("status", ticket.getTicketStatus().toString());
    if (ticket.getTextChannel() != null) {
      document.append("channel", ticket.getTextChannel().getIdLong());
    }
    Document query = new Document("id", ticket.getId());
    Document first = this.tickets.find(query).first();
    if (first != null) {
      this.tickets.replaceOne(query, document);
    } else {
      this.tickets.insertOne(document);
    }
  }

  /**
   * Listens to when a ticket is done to save it in the database
   *
   * @param event the event of a ticket being done
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTicketStatusUpdated(@NotNull TicketStatusUpdatedEvent event) {
    if (!event.isCancelled() && event.getStatus() == TicketStatus.OPEN) {
      this.saveTicket(event.getTicket());
    }
  }

  /**
   * Listen to when a user is unloaded to save it to the database
   *
   * @param event the event of a user being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotUserUnloaded(BotUserUnloadedEvent event) {
    BotUser user = event.getUser();
    Document document =
        new Document("id", user.getId())
            .append("preferences", this.preferencesToDocument(user))
            .append("permissions", this.getPermissionStacksDocument(user));
    Document query = new Document("id", user.getId());
    Document first = this.users.find(query).first();
    if (first != null) {
      this.users.replaceOne(first, document);
    } else {
      this.users.insertOne(document);
    }
  }

  /**
   * Listens for messages being unloaded to upload them to the database
   *
   * @param event the event of a message being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotMessageUnloaded(BotMessageUnloadedEvent event) {
    BotResponsiveMessage message = event.getMessage();
    Document query = new Document("id", message.getId());
    Document document =
        new Document("id", message.getId()).append("type", message.getType()).append("data", this.dataToDocument(message));
    Document first = this.messages.find(query).first();
    if (first != null) {
      this.messages.replaceOne(query, document);
    } else {
      this.messages.insertOne(document);
    }
  }

  /**
   * Convert the data from a responsive message into a document
   *
   * @param message the message to convert the data from
   * @return the data as a document
   */
  @NotNull
  private Document dataToDocument(@NotNull BotResponsiveMessage message) {
    Document document = new Document();
    message.getData().getMap().forEach(document::append);
    return document;
  }

  /**
   * Get the document of a permission stack from a permissible
   *
   * @param permissible the permissible to get the stacks from
   * @return the document of the permission stack
   */
  @NotNull
  private List<Document> getPermissionStacksDocument(@NotNull Permissible permissible) {
    List<Document> stack = new ArrayList<>();
    for (PermissionStack permission : permissible.getPermissions()) {
      List<String> nodes = new ArrayList<>();
      for (Permission permissionPermission : permission.getPermissions()) {
        nodes.add(permissionPermission.getNodeAppended());
      }
      stack.add(new Document("context", permission.getContext()).append("permissions", nodes));
    }
    return stack;
  }

  /**
   * Convert the users from a ticket into a document
   *
   * @param ticket the ticket to get the document from
   * @return the document
   */
  @NotNull
  private Document usersToDocument(@NotNull Ticket ticket) {
    Document document = new Document();
    ticket.getUsers().forEach((user, role) -> document.append(String.valueOf(user.getId()), role));
    return document;
  }

  /**
   * Get the details of a ticket as a document. {@link
   * com.starfishst.bot.handlers.questions.QuestionRole} simple starts with 'role' so it is safe to
   * cast
   *
   * @param ticket the ticket to get the document from
   * @return the document
   */
  @NotNull
  @SuppressWarnings("unchecked")
  private Document detailsToDocument(@NotNull Ticket ticket) {
    Document document = new Document();
    ticket
        .getDetails()
        .getMap()
        .forEach(
            (key, value) -> {
              if (key.startsWith("role")) {
                List<Long> ids = new ArrayList<>();
                for (Role role : ((List<Role>) value)) {
                  ids.add(role.getIdLong());
                }
                document.append(key, ids);
              } else {
                document.append(key, value);
              }
            });
    return document;
  }

  /**
   * Convert the preferences of a user into a mongo document
   *
   * @param user the user to get the document from
   * @return the document
   */
  @NotNull
  private Document preferencesToDocument(@NotNull BotUser user) {
    Document document = new Document();
    user.getPreferences().getMap().forEach(document::append);
    return document;
  }

  /**
   * Get a responsive message from a query
   *
   * @param query the query to get the message from
   * @return the message if found else null
   */
  @Nullable
  public ResponsiveMessage getResponsiveMessage(@NotNull Document query) {
    Document first = this.messages.find(query).first();
    if (first != null) {
      return this.deserializers
          .get(first.getString("type"))
          .getMessage(first.getLong("id"), this.getPreferences(first, "data"), first);
    } else {
      return null;
    }
  }

  @Override
  public boolean acceptBots() {
    return false;
  }

  @Override
  public @NotNull Collection<ResponsiveMessage> getResponsiveMessages(@Nullable Guild guild) {
    return new ArrayList<>();
  }

  @Override
  @Nullable
  public ResponsiveMessage getResponsiveMessage(Guild guild, long messageId) {
    StarfishResponsiveMessage message =
        Cache.getCatchable(
            catchable ->
                catchable instanceof StarfishResponsiveMessage
                    && ((StarfishResponsiveMessage) catchable).getId() == messageId,
            StarfishResponsiveMessage.class);
    if (message != null) {
      return message.refresh();
    }
    return this.getResponsiveMessage(new Document("id", messageId));
  }

  @Override
  public void onUnload() {
    this.client.close();
  }

  @Override
  public String getName() {
    return "mongo-data-loader";
  }

  @Override
  public @Nullable Ticket getTicket(long id) {
    StarfishTicket ticket =
        Cache.getCatchable(
            catchable ->
                catchable instanceof StarfishTicket && ((StarfishTicket) catchable).getId() == id,
            StarfishTicket.class);
    if (ticket != null) {
      return ticket;
    }
    return this.getTicket(new Document("id", id));
  }

  @Override
  public @Nullable Ticket getTicketByChannel(long channelId) {
    return this.getTicket(new Document("channel", channelId));
  }

  @Override
  public @NotNull BotUser getStarfishUser(long id) {
    BotUser user =
        Cache.getCatchable(
            catchable ->
                catchable instanceof StarfishUser && ((StarfishUser) catchable).getId() == id,
            StarfishUser.class);
    if (user != null) {
      return user;
    }
    user = this.getStarfishUser(new Document("id", id));
    if (user != null) {
      return user;
    } else {
      return new StarfishUser(
          id, new StarfishValuesMap(Maps.singleton("lang", "en")), new HashSet<>());
    }
  }

  @Override
  public @NotNull BotRole getStarfishRole(long id) {
    BotRole role =
        Cache.getCatchable(
            catchable ->
                catchable instanceof StarfishRole && ((StarfishRole) catchable).getId() == id,
            StarfishRole.class);
    if (role != null) {
      return role;
    }
    role = this.getStarfishRole(new Document("id", id));
    if (role != null) {
      return role;
    } else {
      return new StarfishRole(id, new HashSet<>());
    }
  }

  @Override
  public void deleteMessage(@NotNull ResponsiveMessage message) {
    Document query = new Document("id", message.getId());
    this.messages.deleteOne(query);
  }

  @SubscribeEvent
  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    StarfishLoader.super.onMessageReactionAdd(event);
  }
}
