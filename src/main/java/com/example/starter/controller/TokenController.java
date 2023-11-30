package com.example.starter.controller;

import com.example.starter.jwtauth.JWTAuthProvider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

public class TokenController {

  private final JWTAuthProvider jwtAuthProvider;
  private final JWTAuth jwtAuth;
  private final MongoDatabase database;

  public TokenController(JWTAuth jwtAuth, JWTAuthProvider jwtAuthProvider, MongoDatabase database) {
    this.jwtAuth = jwtAuth;
    this.jwtAuthProvider = jwtAuthProvider;
    this.database = database;
  }

  public void generateTokens(RoutingContext ctx) {
    String accessToken = jwtAuth.generateToken(
      new JsonObject().put("sub", "example-user"),
      new JWTOptions().setExpiresInMinutes(30)
    );

    String refreshToken = jwtAuth.generateToken(
      new JsonObject().put("sub", "example-user-refresh"),
      new JWTOptions().setExpiresInMinutes(1440)
    );

    System.out.println("Token JWT (Access Token): " + accessToken);
    System.out.println("Refresh Token: " + refreshToken);

    saveRefreshTokenToDatabase(refreshToken);
    ctx.response().putHeader("content-type", "text/plain").end("Token JWT (Access Token): " + accessToken);
  }

  public void secureEndpoint(RoutingContext ctx) {
    jwtAuthProvider.authenticate(ctx);

    ctx.response().setStatusCode(200).end("Zabezpieczony endpoint.");
  }

  private void saveRefreshTokenToDatabase(String refreshToken) {
    MongoCollection<Document> refreshTokenCollection = database.getCollection("users");
    Document refreshTokenDocument = new Document()
      .append("token", refreshToken);

    refreshTokenCollection.insertOne(refreshTokenDocument);
  }

}
