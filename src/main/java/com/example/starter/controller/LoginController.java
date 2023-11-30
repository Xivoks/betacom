package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import com.example.starter.service.AuthenticationService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

public class LoginController {

  private final JWTAuth jwtAuth;
  private final AuthenticationService authService;
  private final MongoDatabaseManager databaseManager;

  public LoginController(JWTAuth jwtAuth, AuthenticationService authService, MongoDatabaseManager databaseManager) {
    this.jwtAuth = jwtAuth;
    this.authService = authService;
    this.databaseManager = databaseManager;
  }

  public void register(Router router) {
    router.post("/login").handler(this::login);
  }

  public void login(RoutingContext ctx) {
    JsonObject requestBody = ctx.getBodyAsJson();
    System.out.println("Request Body: " + requestBody);

    if (requestBody != null) {
      String login = requestBody.getString("login");
      String password = requestBody.getString("password");

      System.out.println("Login: " + login);
      System.out.println("Password: " + password);

      if (authService.verifyLogin(login, password)) {
        Document userDocument = getUserDocumentFromDatabase(login);

        if (userDocument != null) {
          String userId = userDocument.getString("id");
          String refreshToken = jwtAuth.generateToken(
            new JsonObject().put("sub", login).put("userId", userId),
            new JWTOptions().setExpiresInMinutes(1440)
          );

          if (updateRefreshTokenInDatabase(login, refreshToken)) {
            JsonObject response = new JsonObject().put("token", refreshToken).put("message", "Witaj, " + login);

            ctx.response().putHeader("content-type", "application/json").setStatusCode(200).end(response.encode());
          } else {
            ctx.response().setStatusCode(500).end("Failed to update refreshToken");
          }
        } else {
          ctx.response().setStatusCode(500).end("User ID not found");
        }
      } else {
        ctx.response().setStatusCode(401).end("Authentication failed");
      }
    } else {
      ctx.response().setStatusCode(400).end("Invalid request body format");
    }
  }

  private Document getUserDocumentFromDatabase(String login) {
    MongoCollection<Document> usersCollection = databaseManager.getCollection("users");

    Document query = new Document("login", login);
    return usersCollection.find(query).first();
  }

  private boolean updateRefreshTokenInDatabase(String login, String refreshToken) {
    MongoCollection<Document> usersCollection = databaseManager.getCollection("users");

    Document query = new Document("login", login);
    Document update = new Document("$set", new Document("token", refreshToken));

    UpdateResult updateResult = usersCollection.updateOne(query, update);

    return updateResult.getModifiedCount() > 0;
  }
}
