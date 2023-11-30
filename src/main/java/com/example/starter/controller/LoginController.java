package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import com.example.starter.service.AuthenticationService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.bson.conversions.Bson;

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

        String refreshToken = jwtAuth.generateToken(
          new JsonObject().put("sub", "example-user-refresh"),
          new JWTOptions().setExpiresInMinutes(1440)
        );
        updateRefreshTokenInDatabase(login, refreshToken);

        JsonObject response = new JsonObject().put("token", refreshToken).put("message", "Witaj, " + login);

        ctx.response().putHeader("content-type", "application/json").setStatusCode(200).end(response.encode());
      } else {
        ctx.response().setStatusCode(401).end("Authentication failed");
      }
    } else {
      ctx.response().setStatusCode(400).end("Invalid request body format");
    }
  }
  private void updateRefreshTokenInDatabase(String userId, String refreshToken) {
    MongoCollection<Document> refreshTokenCollection = databaseManager.getCollection("users");

    Bson filter = Filters.eq("login", userId);

    Document updateDocument = new Document("$set", new Document("token", refreshToken));

    UpdateResult updateResult = refreshTokenCollection.updateOne(filter, updateDocument);

    if (updateResult.getModifiedCount() == 1) {
      System.out.println("RefreshToken został zaktualizowany.");
    } else {
      System.out.println("Nie udało się zaktualizować refreshToken.");
    }
  }
}
