package com.example.starter.controller;

import com.example.starter.jwtauth.JWTAuthProvider;
import com.example.starter.service.TokenService;
import com.mongodb.client.MongoCollection;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

public class TokenController {

  private final TokenService tokenService;

  public TokenController(JWTAuth jwtAuth, JWTAuthProvider jwtAuthProvider, MongoCollection<Document> refreshTokenCollection) {
    this.tokenService = new TokenService(jwtAuth, jwtAuthProvider, refreshTokenCollection);
  }

  public void generateTokens(RoutingContext ctx) {
    String accessToken = tokenService.generateAccessToken();
    String refreshToken = tokenService.generateRefreshToken();

    System.out.println("Token JWT (Access Token): " + accessToken);
    System.out.println("Refresh Token: " + refreshToken);

    tokenService.saveRefreshTokenToDatabase(refreshToken);
    ctx.response().putHeader("content-type", "text/plain").end("Token JWT (Access Token): " + accessToken);
  }

  public void secureEndpoint(RoutingContext ctx) {
    tokenService.secureEndpoint(ctx);
  }
}
