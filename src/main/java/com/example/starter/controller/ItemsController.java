package com.example.starter.controller;

import com.example.starter.service.ItemsService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

import java.util.List;

public class ItemsController {
  private final JWTAuth jwtAuth;
  private final ItemsService itemsService;

  public ItemsController(JWTAuth jwtAuth, ItemsService itemsService) {
    this.jwtAuth = jwtAuth;
    this.itemsService = itemsService;
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

          Document itemDocument = itemsService.createItem(userId, itemName);

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

        List<Document> userItems = itemsService.getItemsByOwner(userId);

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
