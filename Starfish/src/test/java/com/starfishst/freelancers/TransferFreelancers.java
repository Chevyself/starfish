package com.starfishst.freelancers;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import org.bson.Document;

public class TransferFreelancers {

  public static void main(String[] args) {
    String uri = "mongodb+srv://Chevy:1004133609@starfish.ig1ce.mongodb.net/test";
    MongoClient client = new MongoClient(new MongoClientURI(uri));
    MongoDatabase database = client.getDatabase("Starfish-3-Test");
    MongoCollection<Document> freelancers = database.getCollection("freelancers");
    MongoCollection<Document> users = database.getCollection("users");
    MongoCursor<Document> cursor = freelancers.find().cursor();
    System.out.println(cursor.hasNext());
    while (cursor.hasNext()) {
      Document document = cursor.next();
      System.out.println("Copying " + document);
      Document preferences = new Document("freelancer", true);
      preferences.put("portfolio", document.getList("portfolio", String.class, new ArrayList<>()));
      Document copy = new Document("id", document.getLong("id"));
      copy.put("preferences", preferences);
      copy.put("permissions", new ArrayList<>());
      users.insertOne(copy);
    }
  }
}
