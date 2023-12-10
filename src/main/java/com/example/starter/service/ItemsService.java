package com.example.starter.service;

import com.example.starter.database.MongoDatabaseManager;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemsService {
  private final MongoDatabaseManager databaseManager;

  public ItemsService(MongoDatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public Document createItem(String userId, String itemName) {
    Document itemDocument = new Document()
      .append("id", UUID.randomUUID().toString())
      .append("owner", userId)
      .append("name", itemName);

    databaseManager.getCollection("items").insertOne(itemDocument);

    return itemDocument;
  }

  public List<Document> getItemsByOwner(String userId) {
    MongoCollection<Document> itemsCollection = databaseManager.getCollection("items");
    Document query = new Document("owner", userId);

    return itemsCollection.find(query).into(new ArrayList<>());
  }
}
