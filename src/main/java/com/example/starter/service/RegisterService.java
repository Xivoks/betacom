package com.example.starter.service;

import com.example.starter.database.MongoDatabaseManager;
import com.mongodb.client.MongoCollection;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class RegisterService {
  private final MongoDatabaseManager databaseManager;
  private final JWTAuth jwtAuth;

  public RegisterService(MongoDatabaseManager databaseManager, JWTAuth jwtAuth) {
    this.databaseManager = databaseManager;
    this.jwtAuth = jwtAuth;
  }

  public boolean registerUser(String login, String password) {
    if (!userExistsInDatabase(login)) {
      String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
      saveUserToDatabase(login, hashedPassword);
      return true;
    } else {
      return false;
    }
  }

  private boolean userExistsInDatabase(String login) {
    MongoCollection<Document> usersCollection = databaseManager.getCollection("users");
    Document query = new Document("login", login);
    long count = usersCollection.countDocuments(query);
    return count > 0;
  }

  private void saveUserToDatabase(String login, String hashedPassword) {
    MongoCollection<Document> usersCollection = databaseManager.getCollection("users");
    String token = jwtAuth.generateToken(
      new JsonObject().put("sub", login),
      new JWTOptions().setExpiresInMinutes(1440)
    );
    UUID userId = UUID.randomUUID();
    Document userDocument = new Document()
      .append("id", userId.toString())
      .append("login", login)
      .append("password", hashedPassword)
      .append("token", token);

    usersCollection.insertOne(userDocument);
  }
}
