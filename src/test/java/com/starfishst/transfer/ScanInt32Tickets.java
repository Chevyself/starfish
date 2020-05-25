package com.starfishst.transfer;

import com.mongodb.client.MongoCursor;
import com.starfishst.bot.tickets.loader.mongo.MongoTicketLoader;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * Scan for tickets that have int32 and not the expected int64
 */
public class ScanInt32Tickets {

  public static void main(String[] args) {
    MongoTicketLoader loader =
        new MongoTicketLoader(
            "",
            "");
    MongoCursor<Document> cursor = loader.getDatabase().getCollection("tickets").find().cursor();
    while (cursor.hasNext()) {
      printIfLong(cursor.next());
    }
  }

    /**
     * Print the key that contains the int32
     *
     * @param document the document looking for the key
     */
  public static void printIfLong(@NotNull Document document) {
    document
        .keySet()
        .forEach(
            key -> {
              if (document.get(key) instanceof Document) {
                printIfLong(document.get(key, Document.class));
              } else {
                if (document.get(key) instanceof Integer) {
                  System.out.println(document.getInteger(key));
                }
              }
            });
  }
}
