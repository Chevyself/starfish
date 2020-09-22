package com.starfishst.bot.tickets;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.users.StarfishPreferences;
import com.starfishst.bot.users.StarfishUser;
import com.starfishst.core.utils.time.Time;
import net.dv8tion.jda.api.JDA;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class StarfishLoader implements DataLoader {

    /**
     * The jda instance to get important information of discord
     */
    @NotNull
    private final JDA jda;
    /** The mongo client created for the use of the database */
    @NotNull private final MongoClient client;
    /**
     * The database that is being used
     */
    @NotNull
    private final MongoDatabase database;
    /** The collection to work with tickets */
    @NotNull private final MongoCollection<Document> tickets;
    /** The collection to work with freelancers */
    @NotNull private final MongoCollection<Document> freelancers;

    /**
     * Creates the mongo loader instance
     *
     * @param jda the jda instance for important discord information
     * @param uri the mongo uri for authentication
     * @param database the database to use
     */
    public StarfishLoader(@NotNull JDA jda, @NotNull String uri, @NotNull String database) {
        this.jda = jda;
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectTimeout(300).sslEnabled(true);
        this.client = new MongoClient(new MongoClientURI(uri, builder));
        this.database = client.getDatabase(database);
        this.tickets = this.database.getCollection("tickets");
        this.freelancers = this.database.getCollection("freelancers");
        this.ping();
    }

    /** Ping the mongo database to make sure that this is working */
    public void ping() {
        database.runCommand(new Document("serverStatus", 1));
    }

    @Nullable
    public BotUser getStarfishUser(@NotNull Document query) {
        return null;
    }

    @Nullable
    public Ticket getTicket(@NotNull Document query) {
        Document first = this.tickets.find(query).first();
        // TODO make this use configuration

        HashMap<BotUser, String> usersMap = new HashMap<>();
        if (first != null) {
            if (first.get("users") != null) {
                Document users = first.get("users", Document.class);
                for (String key : users.keySet()) {
                    BotUser user = this.getStarfishUser(Long.parseLong(key));
                    usersMap.put(user, users.getString(key));
                }
            }
            return new StarfishTicket(Time.fromString("30m"), query.getLong("id"), TicketType.valueOf(query.getString("type").toUpperCase()), TicketStatus.valueOf(query.getString("type").toUpperCase()), usersMap, jda.getTextChannelById(first.get("channel") != null ? first.getLong("channel") : -1));
        }
        return null;
    }

    @Override
    public @Nullable Ticket getTicket(long id) {
        return this.getTicket(new Document("id", id));
    }

    @Override
    public @NotNull BotUser getStarfishUser(long id) {
        BotUser user = this.getStarfishUser(new Document("id", id));
        if (user != null) {
            return user;
        }
        return new StarfishUser(id, new StarfishPreferences(new HashMap<>()));
    }

}
