package com.example.starter.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

@Getter
public class MongoDatabaseManager {
  private final MongoClient mongoClient;
  private final MongoDatabase database;

  public MongoDatabaseManager() {
    mongoClient = MongoClients.create("mongodb://localhost:27017");
    database = mongoClient.getDatabase("betacom");
  }

  public void close() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }

  public MongoCollection<Document> getCollection(String collectionName) {
    return database.getCollection(collectionName);
  }
}
