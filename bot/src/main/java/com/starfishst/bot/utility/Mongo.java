package com.starfishst.bot.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.starfishst.adapters.ColorAdapter;
import com.starfishst.adapters.LongMongoAdapter;
import com.starfishst.adapters.PermissionAdapter;
import com.starfishst.adapters.PermissionStackDeserializer;
import com.starfishst.adapters.QuestionAdapter;
import com.starfishst.adapters.ReactionResponseAdapter;
import com.starfishst.adapters.ResponsiveMessageDeserializer;
import com.starfishst.adapters.ValuesMapAdapter;
import com.starfishst.api.Starfish;
import com.starfishst.api.permissions.Permission;
import com.starfishst.api.permissions.PermissionStack;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.data.StarfishPermission;
import com.starfishst.bot.handlers.questions.Question;
import com.starfishst.commands.jda.utils.responsive.ReactionResponse;
import com.starfishst.commands.jda.utils.responsive.ResponsiveMessage;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.cache.Cache;
import me.googas.commons.gson.adapters.time.TimeAdapter;
import me.googas.commons.time.Time;
import org.bson.Document;

/** Static utilities for mongo */
public class Mongo {

  @NonNull public static final Gson GSON = Mongo.createGson();

  /**
   * Creates a gson instance for mongo
   *
   * @return the created gson instance
   */
  @NonNull
  public static Gson createGson() {
    LongMongoAdapter longAdapter = new LongMongoAdapter();
    ValuesMapAdapter valuesMapAdapter = new ValuesMapAdapter();
    return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Color.class, new ColorAdapter())
        .registerTypeAdapter(Long.class, longAdapter)
        .registerTypeAdapter(long.class, longAdapter)
        .registerTypeAdapter(Permission.class, new PermissionAdapter())
        .registerTypeAdapter(StarfishPermission.class, new PermissionAdapter())
        .registerTypeAdapter(PermissionStack.class, new PermissionStackDeserializer())
        .registerTypeAdapter(ValuesMap.class, valuesMapAdapter)
        .registerTypeAdapter(Question.class, new QuestionAdapter())
        .registerTypeAdapter(ReactionResponse.class, new ReactionResponseAdapter())
        .registerTypeAdapter(ResponsiveMessage.class, new ResponsiveMessageDeserializer())
        .registerTypeAdapter(ValuesMap.class, valuesMapAdapter)
        .registerTypeAdapter(Time.class, new TimeAdapter())
        .create();
  }

  public static void save(
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @NonNull Object object) {
    Document document = Document.parse(Mongo.GSON.toJson(object));
    Document first = collection.find(query).first();
    if (first != null) {
      collection.replaceOne(query, document);
    } else {
      collection.insertOne(document);
    }
  }

  @Nullable
  public static <T extends StarfishCatchable> T get(
      @NonNull Class<T> typeOfT,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @NonNull Predicate<T> predicate) {
    return Starfish.getCache()
        .getOrSupply(typeOfT, predicate, () -> Mongo.getCatchable(typeOfT, collection, query));
  }

  @Nullable
  public static <T extends StarfishCatchable> T getCatchable(
      @NonNull Class<T> typeOfT,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query) {
    Document first = collection.find(query).first();
    if (first == null) return null;
    T t = Mongo.getObject(typeOfT, first);
    T t1 = Starfish.getCache().get(typeOfT, catchable -> catchable.equals(t));
    if (t1 != null) return t1;
    if (t != null) t.cache();
    return t;
  }

  /**
   * Get an object given a document
   *
   * @param typeOfT the type of object to get from document
   * @param document the document to get the object from
   * @param <T> the type of the object
   * @return the object given by json
   */
  @Nullable
  public static <T> T getObject(@NonNull Type typeOfT, @NonNull Document document) {
    try {
      return Mongo.GSON.fromJson(document.toJson(), typeOfT);
    } catch (JsonSyntaxException e) {
      return null;
    }
  }

  @NonNull
  public static <T> List<T> getMany(
      @NonNull Class<T> typeOfT,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @Nullable Document sort,
      int page,
      int limit) {
    List<T> list = new ArrayList<>();
    FindIterable<Document> find = collection.find(query);
    if (page != -1 && limit != -1) {
      find.limit(limit).skip(page * limit);
    }
    if (sort != null) {
      find.sort(sort);
    }
    MongoCursor<Document> cursor = find.cursor();
    while (cursor.hasNext()) {
      T obj = Mongo.getObject(typeOfT, cursor.next());
      if (obj != null) {
        list.add(obj);
      }
    }
    return list;
  }

  /**
   * Check whether a catchable is contained inside a list
   *
   * @param list the list to see if contains the catchable
   * @param catchable the catchable to see if it is contained
   * @param <T> the type of the catchable
   * @return true if it is inside the list
   */
  public static <T extends StarfishCatchable> boolean contains(@NonNull List<T> list, T catchable) {
    for (T added : list) {
      if (catchable.equals(added)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Delete an object from a collection with the given query
   *
   * @param collection the collection to delete the object from
   * @param query the way to identify the document
   * @return true if at least 1 document was deleted
   */
  public static boolean delete(
      @NonNull MongoCollection<Document> collection, @NonNull Document query) {
    return collection.deleteMany(query).getDeletedCount() > 0;
  }

  /**
   * Count the amount of documents that there's with a query
   *
   * @param collection the collection to send the query
   * @param query the query to match the object
   * @return the amount of documents found with the query
   */
  public static long count(@NonNull MongoCollection<Document> collection, @NonNull Document query) {
    return collection.countDocuments(query);
  }

  @NonNull
  public static <T extends StarfishCatchable> List<T> getMany(
      @NonNull Class<T> clazz,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @Nullable Document sort,
      int page,
      int size,
      @NonNull Predicate<T> predicate) {
    Cache cache = Starfish.getCache();
    List<T> many = new ArrayList<>(cache.getMany(clazz, predicate));
    List<T> doc = Mongo.getMany(clazz, collection, query, sort, page, size);
    for (T t : doc) {
      if (!Mongo.contains(many, t)) {
        many.add(t);
        if (!cache.contains(t)) t.cache();
      }
    }
    return many;
  }
}
