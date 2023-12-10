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
    router.post("/register").handler(this::handleRegistration);
  }

  public void handleRegistration(RoutingContext ctx) {
    JsonObject requestBody = ctx.body().asJsonObject();

    if (requestBody != null) {
      String login = requestBody.getString("login");
      String password = requestBody.getString("password");

      if (login == null || password == null) {
        ctx.response().setStatusCode(400)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", "Login and password must be provided").encode());
        return;
      }

      boolean registrationResult = registerService.registerUser(login, password);

      if (registrationResult) {
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
