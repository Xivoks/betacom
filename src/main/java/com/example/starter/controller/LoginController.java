package com.example.starter.controller;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class LoginController {
  public static void register(Router router) {
    router.post("/login").handler(LoginController::login);
  }

  private static void login(RoutingContext context) {
    context.response()
      .putHeader("content-type", "application/json")
      .end("{\"token\":\"exampleToken\"}");
  }
}
