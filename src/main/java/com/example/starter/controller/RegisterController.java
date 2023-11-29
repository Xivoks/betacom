package com.example.starter.controller;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RegisterController {
  public static void register(Router router) {
    router.post("/register").handler(RegisterController::registerUser);
  }

  private static void registerUser(RoutingContext context) {
    String login = context.request().getParam("login");
    String password = context.request().getParam("password");

    context.response()
      .putHeader("content-type", "application/json")
      .end("{\"message\":\"User registered successfully\"}");
  }
}
