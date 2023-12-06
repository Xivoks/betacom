package com.example.starter.controller;

import com.example.starter.service.RegisterService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RegisterController {
  private final RegisterService registerService;

  public RegisterController(RegisterService registerService) {
    this.registerService = registerService;
  }

  public void register(Router router) {
    router.post("/register").handler(this::register);
  }

  private void register(RoutingContext ctx) {
    JsonObject requestBody = ctx.getBodyAsJson();
    if (requestBody != null) {
      String login = requestBody.getString("login");
      String password = requestBody.getString("password");

      if (registerService.registerUser(login, password)) {
        JsonObject response = new JsonObject().put("message", "Konto zostało utworzone");
        ctx.response().putHeader("content-type", "application/json").setStatusCode(201).end(response.encode());
      } else {
        ctx.response().setStatusCode(400).end("Użytkownik o podanym loginie już istnieje");
      }
    } else {
      ctx.response().setStatusCode(400).end("Invalid request body format");
    }
  }
}
