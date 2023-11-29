package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DataController {
  private MongoDatabaseManager databaseManager;

  public DataController(MongoDatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public void insertData(String collectionName, Document document) {
    try (MongoClient mongoClient = databaseManager.getMongoClient()) {
      MongoDatabase database = mongoClient.getDatabase("betacom");
      database.getCollection(collectionName).insertOne(document);
    }
  }
}
