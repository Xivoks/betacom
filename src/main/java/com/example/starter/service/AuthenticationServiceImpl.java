package com.example.starter.service;

import com.example.starter.database.MongoDatabaseManager;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import static com.mongodb.client.model.Filters.eq;

public class AuthenticationServiceImpl implements AuthenticationService {

  private final MongoDatabaseManager databaseManager;

  public AuthenticationServiceImpl(MongoDatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  @Override
  public boolean verifyLogin(String login, String password) {
    MongoCollection<Document> usersCollection = databaseManager.getCollection("users");

    Document userDocument = usersCollection.find(eq("login", login)).first();

    if (userDocument != null) {
      String storedPasswordHash = userDocument.getString("password");
        return BCrypt.checkpw(password, storedPasswordHash);
    }

    return false;
  }
}
