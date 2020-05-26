package com.starfishst.transfer;

import com.starfishst.bot.tickets.loader.mongo.MongoTicketLoader;
import org.bson.Document;

/**
 * Update the channel of a ticket
 */
public class UpdateChannel {

    /**
     * Updates the channel of a ticket
     * @param args no args
     */
    public static void main(String[] args) {
        Document query = new Document("id", 1260L);
        Document update = new Document("$set", new Document("channel", 708987268022403102L));
        MongoTicketLoader loader = new MongoTicketLoader("", "");
        loader.getDatabase().getCollection("tickets").updateOne(query, update);
    }

}
