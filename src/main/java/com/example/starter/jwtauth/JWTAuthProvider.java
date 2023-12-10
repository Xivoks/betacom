package com.example.starter.jwtauth;


import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

public class JWTAuthProvider {

  private final JWTAuth jwtAuth;

  public JWTAuthProvider(JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }

  public void authenticate(RoutingContext ctx) {
    String token = ctx.request().getHeader("Authorization");

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
      jwtAuth.authenticate(new JsonObject().put("token", token), res -> {
        if (res.succeeded()) {
          ctx.next();
        } else {
          ctx.response().setStatusCode(401).end("Błąd uwierzytelniania JWT.");
        }
      });
    } else {
      ctx.response().setStatusCode(401).end("Brak tokena JWT w nagłówku autoryzacji.");
    }
  }
}
