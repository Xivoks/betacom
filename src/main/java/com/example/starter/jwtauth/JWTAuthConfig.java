package com.example.starter.jwtauth;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public class JWTAuthConfig {
  public static JWTAuth createJWTAuthProvider(Vertx vertx) {
    JWTAuthOptions config = new JWTAuthOptions()
      .addPubSecKey(new PubSecKeyOptions()
        .setAlgorithm("HS256")
        .setBuffer("secret-key"));

    return JWTAuth.create(vertx, config);
  }
}
