package com.example.starter.controller;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;

public class ItemsController {
  private static List<String> items = new ArrayList<>();

  public static void register(Router router) {
    router.post("/items").handler(ItemsController::createItem);
    router.get("/items").handler(ItemsController::getItems);
  }

  private static void createItem(RoutingContext context) {
    String itemName = context.request().getParam("itemName");

    items.add(itemName);

    context.response()
      .putHeader("content-type", "application/json")
      .end("{\"message\":\"Item created successfully\"}");
  }

  private static void getItems(RoutingContext context) {
    context.response()
      .putHeader("content-type", "application/json")
      .end("{\"items\":" + items.toString() + "}");
  }
}
