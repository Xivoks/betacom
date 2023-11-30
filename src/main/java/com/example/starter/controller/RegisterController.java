package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import com.mongodb.client.MongoCollection;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController {
  private final MongoDatabaseManager databaseManager;
  private final JWTAuth jwtAuth;

  public RegisterController(MongoDatabaseManager databaseManager, JWTAuth jwtAuth) {
    this.databaseManager = databaseManager;
    this.jwtAuth = jwtAuth;
  }

  public void register(RoutingContext ctx) {

    JsonObject requestBody = ctx.getBodyAsJson();

    if (requestBody != null) {
      String login = requestBody.getString("login");
      String password = requestBody.getString("password");

      System.out.println("Login: " + login);
      System.out.println("Password: " + password);

      if (!userExistsInDatabase(login)) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        saveUserToDatabase(login, hashedPassword);

        JsonObject response = new JsonObject().put("message", "Konto zostało utworzone");

        ctx.response().putHeader("content-type", "application/json").setStatusCode(201).end(response.encode());
      } else {
        ctx.response().setStatusCode(400).end("Użytkownik o podanym loginie już istnieje");
      }
    } else {
      ctx.response().setStatusCode(400).end("Invalid request body format");
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
      new JsonObject().put("sub", "example-user-refresh"),
      new JWTOptions().setExpiresInMinutes(1440)
    );
    Document userDocument = new Document()
      .append("login", login)
      .append("password", hashedPassword)
      .append("token",token);

    usersCollection.insertOne(userDocument);
  }

}
