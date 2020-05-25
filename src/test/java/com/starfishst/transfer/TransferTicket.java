package com.starfishst.transfer;

import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.tickets.loader.mongo.MongoTicketLoader;
import com.starfishst.simple.Lots;
import java.util.HashMap;
import org.bson.Document;

/**
 * Creates a new ticket (Probably missing ticket)
 */
public class TransferTicket {

  /**
   * Main java
   *
   * @param args no ags
   */
  public static void main(String[] args) {
    long id = 1211;
    long user = 423991301764153355L;
    TicketStatus status = TicketStatus.OPEN;
    TicketType type = TicketType.ORDER;
    HashMap<String, Object> details = new HashMap<>();
    details.put("title", "");
    details.put(
        "description",
        "");
    details.put("roles", Lots.list());
    details.put("budget", "");
    details.put("time-frame", "");
    Document document = new Document("id", id);
    document.put("user", user);
    document.put("status", status.toString());
    document.put("type", type.toString());
    document.put("details", details);

    MongoTicketLoader loader = new MongoTicketLoader("-", "-");
    loader.getDatabase().getCollection("tickets").insertOne(document);
    System.out.println("end");
  }

}
