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
    long id = 1260;
    long user = 290246051514810369L;
    TicketStatus status = TicketStatus.OPEN;
    TicketType type = TicketType.ORDER;
    HashMap<String, Object> details = new HashMap<>();
    details.put("title", "Spawn");
    details.put(
        "description",
        "A floating island, with a hole that leads straight to the void meant to be a replacement for a wilderness portal.\n" +
                "\n" +
                "The theme should be hellish, meaning a lot of red and dark colors. As for the buildings there needs to be buildins to all the following:\n" +
                "- An AuctionHouse\n" +
                "-  An Enchanter\n" +
                "- A shop\n" +
                "- A small dungeon with grant entrance\n" +
                "- A pointsshop\n" +
                "- A place to show top players\n" +
                "Other than that there needs to be spikes poking out of the side, bottom and top of the island.");
    details.put("roles", Lots.list(565632754318966785L));
    details.put("budget", "60$");
    details.put("time-frame", "ASAP");
    Document document = new Document("id", id);
    document.put("user", user);
    document.put("status", status.toString());
    document.put("type", type.toString());
    document.put("details", details);

    MongoTicketLoader loader = new MongoTicketLoader("", "");
    loader.getDatabase().getCollection("tickets").insertOne(document);
    System.out.println("end");
  }

}
