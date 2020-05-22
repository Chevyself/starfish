package com.starfishst.transfer;

import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.tickets.loader.mongo.MongoTicketLoader;
import com.starfishst.simple.Lots;
import java.util.HashMap;
import org.bson.Document;

public class TransferTicket {

  public static void main(String[] args) {
    long id = 1211;
    long user = 423991301764153355L;
    TicketStatus status = TicketStatus.OPEN;
    TicketType type = TicketType.ORDER;
    HashMap<String, Object> details = new HashMap<>();
    details.put("title", "Spawn");
    details.put(
        "description",
        " I need a builder to create a magical themed spawn with a medieval taste to it.");
    details.put("roles", Lots.list(565632754318966785L));
    details.put("budget", "40$");
    details.put("time-frame", "ASAP");
    Document document = new Document("id", id);
    document.put("user", user);
    document.put("status", status.toString());
    document.put("type", type.toString());
    document.put("details", details);

    MongoTicketLoader loader =
        new MongoTicketLoader(
            "-",
            "-");
    loader.getDatabase().getCollection("tickets").insertOne(document);
    System.out.println("end");
  }
}
