package com.example.starter.service;

import com.example.starter.jwtauth.JWTAuthProvider;
import com.mongodb.client.MongoCollection;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

public class TokenService {

  private final JWTAuth jwtAuth;
  private final JWTAuthProvider jwtAuthProvider;
  private final MongoCollection<Document> refreshTokenCollection;

  public TokenService(JWTAuth jwtAuth, JWTAuthProvider jwtAuthProvider, MongoCollection<Document> refreshTokenCollection) {
    this.jwtAuth = jwtAuth;
    this.jwtAuthProvider = jwtAuthProvider;
    this.refreshTokenCollection = refreshTokenCollection;
  }

  public String generateAccessToken() {
    return jwtAuth.generateToken(
      new JsonObject().put("sub", "example-user"),
      new JWTOptions().setExpiresInMinutes(30)
    );
  }

  public String generateRefreshToken() {
    return jwtAuth.generateToken(
      new JsonObject().put("sub", "example-user-refresh"),
      new JWTOptions().setExpiresInMinutes(1440)
    );
  }

  public void saveRefreshTokenToDatabase(String refreshToken) {
    Document refreshTokenDocument = new Document()
      .append("token", refreshToken);

    refreshTokenCollection.insertOne(refreshTokenDocument);
  }

  public void secureEndpoint(RoutingContext ctx) {
    jwtAuthProvider.authenticate(ctx);

    ctx.response().setStatusCode(200).end("Zabezpieczony endpoint.");
  }
}
