package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

public class DataController {
  private final MongoDatabaseManager databaseManager;

  public DataController(MongoDatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public void insertData(String collectionName, Document document) {
    try (MongoClient mongoClient = databaseManager.getMongoClient()) {
      MongoDatabase database = mongoClient.getDatabase("betacom");
      database.getCollection(collectionName).insertOne(document);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void handleAddData(RoutingContext context) {
    String collectionName = "betacom";
    Document document = new Document("key", "value");
    insertData(collectionName, document);

    context.response()
      .putHeader("content-type", "application/json")
      .end("Dane zostały dodane do bazy");
  }
}
