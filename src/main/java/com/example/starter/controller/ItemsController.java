package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

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
  }

  private void createItem(RoutingContext ctx) {
    String authorizationHeader = ctx.request().getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String token = authorizationHeader.substring(7);

      jwtAuth.authenticate(new TokenCredentials(token), authResult -> {
        if (authResult.succeeded()) {
          User user = authResult.result();
          String userId = user.principal().getString("sub");

          JsonObject requestBody = ctx.getBodyAsJson();
          if (requestBody != null) {
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
              .setStatusCode(201) // Kod 201 - Created
              .end(response.encode());
          } else {
            ctx.response().setStatusCode(400).end("Invalid request body format");
          }
        } else {
          ctx.response().setStatusCode(401).end("Authentication failed");
        }
      });
    } else {
      ctx.response().setStatusCode(401).end("Unauthorized");
    }
  }
}
