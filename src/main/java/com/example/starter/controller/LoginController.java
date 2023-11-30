package com.example.starter.controller;

import com.example.starter.service.AuthenticationService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class LoginController {

  private final JWTAuth jwtAuth;
  private final AuthenticationService authService;

  public LoginController(JWTAuth jwtAuth, AuthenticationService authService) {
    this.jwtAuth = jwtAuth;
    this.authService = authService;
  }

  public void register(Router router) {
    router.post("/secure/login").handler(this::login);
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
        JsonObject claims = new JsonObject().put("name", "John Doe").put("role", "Student");

        String token = jwtAuth.generateToken(claims);
        JsonObject response = new JsonObject().put("token", token).put("message", "Witaj, " + claims.getString("name"));

        ctx.response().putHeader("content-type", "application/json").setStatusCode(200).end(response.encode());
      } else {
        ctx.response().setStatusCode(401).end("Authentication failed");
      }

    } else {
      ctx.response().setStatusCode(400)
        .end("Invalid request body format");
    }
  }
}
