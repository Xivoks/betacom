package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import com.example.starter.service.AuthenticationService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.http.HttpServerResponse;
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
    try {
      JsonObject requestBody = ctx.body().asJsonObject();

      if (requestBody == null) {
        sendErrorResponse(ctx, 400, "Invalid request body format");
        return;
      }

      String login = requestBody.getString("login");
      String password = requestBody.getString("password");

      if (login == null || password == null) {
        sendErrorResponse(ctx, 400, "Invalid login or password");
        return;
      }

      if (!authService.verifyLogin(login, password)) {
        sendErrorResponse(ctx, 401, "Authentication failed");
        return;
      }

      Document userDocument = getUserDocumentFromDatabase(login);

      if (userDocument == null) {
        sendErrorResponse(ctx, 404, "User not found");
        return;
      }

      String userId = userDocument.getString("id");
      String refreshToken = jwtAuth.generateToken(
        new JsonObject().put("sub", login).put("userId", userId),
        new JWTOptions().setExpiresInMinutes(1440)
      );

      if (!updateRefreshTokenInDatabase(login, refreshToken)) {
        sendErrorResponse(ctx, 400, "Failed to update refreshToken");
        return;
      }

      JsonObject response = new JsonObject().put("token", refreshToken).put("message", "Witaj, " + login);

      HttpServerResponse httpServerResponse = ctx.response();

      httpServerResponse.putHeader("content-type", "application/json");
      httpServerResponse.setStatusCode(200);
      httpServerResponse.end(response.encode());

    } catch (Exception e) {
      sendErrorResponse(ctx, 500, "Internal Server Error");
    }
  }


  private void sendErrorResponse(RoutingContext routingContext, int statusCode, String message) {
    HttpServerResponse response = routingContext.response();

    if (response != null) {
      response.setStatusCode(statusCode);
      response.end(message);
    } else {
      System.err.println("HttpServerResponse is null");
    }
  }

  private Document getUserDocumentFromDatabase(String login) {
    MongoCollection<Document> usersCollection = databaseManager.getCollection("users");

    Document query = new Document("login", login);
    FindIterable<Document> documents = usersCollection.find(query);
    Document result = documents.first();
    return result;
  }

  private boolean updateRefreshTokenInDatabase(String login, String refreshToken) {
    MongoCollection<Document> usersCollection = databaseManager.getCollection("users");

    Document query = new Document("login", login);
    Document update = new Document("$set", new Document("token", refreshToken));

    UpdateResult updateResult = usersCollection.updateOne(query, update);

    return updateResult.getModifiedCount() > 0;
  }
}
