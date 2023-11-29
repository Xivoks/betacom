package com.example.starter.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;

@Getter
public class MongoDatabaseManager {
  private MongoClient mongoClient;

  public MongoDatabaseManager() {
    mongoClient = MongoClients.create("mongodb://localhost:27017");
  }

  public void close() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }
}
