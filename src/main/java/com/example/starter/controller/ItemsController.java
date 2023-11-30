package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import com.mongodb.client.MongoCollection;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemsController {
  private final MongoDatabaseManager databaseManager;
  private final JWTAuth jwtAuth;

  public ItemsController(MongoDatabaseManager databaseManager, JWTAuth jwtAuth) {
    this.databaseManager = databaseManager;
    this.jwtAuth = jwtAuth;
  }

  public void register(Router router) {
    router.post("/secure/items").handler(this::createItem);
    router.get("/secure/items").handler(this::getItems);
  }

  private void createItem(RoutingContext ctx) {
    handleTokenAuthentication(ctx, authResult -> {
      if (authResult.succeeded()) {
        User user = authResult.result();
        String userId = user.principal().getString("sub");

        JsonObject requestBody = ctx.getBodyAsJson();
        if (requestBody != null && requestBody.containsKey("itemName")) {
          String itemName = requestBody.getString("itemName");

          Document itemDocument = new Document()
            .append("id", UUID.randomUUID().toString())
            .append("owner", userId)
            .append("name", itemName);

          databaseManager.getCollection("items").insertOne(itemDocument);

          JsonObject response = new JsonObject()
            .put("message", "Przedmiot został utworzony")
            .put("item", itemDocument);

          ctx.response().putHeader("content-type", "application/json")
            .setStatusCode(201)
            .end(response.encode());
        } else {
          ctx.response().setStatusCode(400).end("Invalid request body format");
        }
      } else {
        ctx.response().setStatusCode(401).end("Authentication failed");
      }
    });
  }

  private void getItems(RoutingContext ctx) {
    handleTokenAuthentication(ctx, authResult -> {
      if (authResult.succeeded()) {
        User user = authResult.result();
        String userId = user.principal().getString("sub");

        MongoCollection<Document> itemsCollection = databaseManager.getCollection("items");
        Document query = new Document("owner", userId);
        List<Document> userItems = itemsCollection.find(query).into(new ArrayList<>());

        JsonObject itemsResponse = new JsonObject()
          .put("items", userItems);

        ctx.response().putHeader("content-type", "application/json")
          .setStatusCode(200)
          .end(itemsResponse.encode());
      } else {
        ctx.response().setStatusCode(401).end("Authentication failed");
      }
    });
  }

  private void handleTokenAuthentication(RoutingContext ctx, Handler<AsyncResult<User>> handler) {
    String authorizationHeader = ctx.request().getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String token = authorizationHeader.substring(7);
      jwtAuth.authenticate(new TokenCredentials(token), handler);
    } else {
      ctx.response().setStatusCode(401).end("Unauthorized");
    }
  }
}
