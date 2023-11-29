package com.example.starter.controller;

import com.example.starter.jwtauth.JWTAuthProvider;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

public class TokenController {

  private final JWTAuthProvider jwtAuthProvider;
  private final JWTAuth jwtAuth;

  public TokenController(JWTAuth jwtAuth, JWTAuthProvider jwtAuthProvider) {
    this.jwtAuth = jwtAuth;
    this.jwtAuthProvider = jwtAuthProvider;
  }

  public void generateToken(RoutingContext ctx) {
    String token = jwtAuth.generateToken(
      new JsonObject().put("sub", "example-user"),
      new JWTOptions().setExpiresInMinutes(30)
    );

    System.out.println("Token JWT: " + token);

    ctx.response().putHeader("content-type", "text/plain").end("Token JWT: " + token);
  }

  public void secureEndpoint(RoutingContext ctx) {
    jwtAuthProvider.authenticate(ctx);

    ctx.response().setStatusCode(200).end("Zabezpieczony endpoint.");
  }
}
