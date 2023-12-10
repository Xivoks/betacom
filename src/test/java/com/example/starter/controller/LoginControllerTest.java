package com.example.starter.controller;

import com.example.starter.database.MongoDatabaseManager;
import com.example.starter.service.AuthenticationService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {


  @Mock
  private JWTAuth jwtAuth;

  @Mock
  private AuthenticationService authService;

  @Mock
  private MongoDatabaseManager databaseManager;

  @InjectMocks
  private LoginController underTest;


  @Test
  void login_InvalidRequestBodyFormat() {
    final RoutingContext ctx = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonObject = null;
    final HttpServerResponse response = mock(HttpServerResponse.class);

    when(ctx.body()).thenReturn(requestBody);
    when(requestBody.asJsonObject()).thenReturn(jsonObject);
    when(ctx.response()).thenReturn(response);

    underTest.login(ctx);

    verify(response).setStatusCode(eq(400));
    verify(response).end(eq("Invalid request body format"));
  }

  @Test
  void login_InvalidLogin() {
    final RoutingContext ctx = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonObject = mock(JsonObject.class);
    final HttpServerResponse response = mock(HttpServerResponse.class);
    final String password = "password";
    final String login = null;

    when(ctx.body())
      .thenReturn(requestBody);

    when(requestBody.asJsonObject())
      .thenReturn(jsonObject);

    when(ctx.response())
      .thenReturn(response);

    when(jsonObject.getString(eq("login")))
      .thenReturn(login);

    when(jsonObject.getString(eq("password")))
      .thenReturn(password);


    underTest.login(ctx);

    verify(response).setStatusCode(eq(400));
    verify(response).end(eq("Invalid login or password"));
  }

  @Test
  void login_AuthenticationFailed() {
    final RoutingContext ctx = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonObject = mock(JsonObject.class);
    final HttpServerResponse response = mock(HttpServerResponse.class);
    final String password = "password";
    final String login = "login";

    when(ctx.body())
      .thenReturn(requestBody);
    when(requestBody.asJsonObject())
      .thenReturn(jsonObject);
    when(ctx.response())
      .thenReturn(response);
    when(jsonObject.getString(eq("login")))
      .thenReturn(login);
    when(jsonObject.getString(eq("password")))
      .thenReturn(password);

    when(authService.verifyLogin(eq(login), anyString()))
      .thenReturn(false);

    underTest.login(ctx);

    verify(response).setStatusCode(eq(401));
    verify(response).end(eq("Authentication failed"));
  }

  @Test
  void login_UserNotFound() {
    final RoutingContext ctx = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonObject = mock(JsonObject.class);
    final HttpServerResponse response = mock(HttpServerResponse.class);
    final String password = "password";
    final String login = "login";
    final MongoCollection<Document> usersCollection = mock(MongoCollection.class);
    final Document query = new Document("login", login);
    final FindIterable<Document> documents = mock(FindIterable.class);
    final Document result = null;

    when(ctx.body())
      .thenReturn(requestBody);
    when(requestBody.asJsonObject())
      .thenReturn(jsonObject);
    when(ctx.response())
      .thenReturn(response);
    when(jsonObject.getString(eq("login")))
      .thenReturn(login);
    when(jsonObject.getString(eq("password")))
      .thenReturn(password);
    when(authService.verifyLogin(eq(login), anyString()))
      .thenReturn(true);
    when(databaseManager.getCollection(anyString()))
      .thenReturn(usersCollection);
    when(usersCollection.find(query))
      .thenReturn(documents);
    when(documents.first())
      .thenReturn(result);


    underTest.login(ctx);

    verify(response).setStatusCode(eq(404));
    verify(response).end(eq("User not found"));
  }

  @Test
  void login_FailedToUpdateRefreshToken() {
    final RoutingContext ctx = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonObject = mock(JsonObject.class);
    final HttpServerResponse response = mock(HttpServerResponse.class);
    final String password = "password";
    final String login = "login";
    final MongoCollection<Document> usersCollection = mock(MongoCollection.class);
    final Document query = new Document("login", login);
    final FindIterable<Document> documents = mock(FindIterable.class);
    final Document userDocument = mock(Document.class);

    final String userId = "userId";
    final String refreshToken = "refreshToken";
    final Document update = new Document("$set", new Document("token", refreshToken));
    final UpdateResult updateResult = mock(UpdateResult.class);

    when(ctx.body())
      .thenReturn(requestBody);
    when(requestBody.asJsonObject())
      .thenReturn(jsonObject);
    when(ctx.response())
      .thenReturn(response);
    when(jsonObject.getString(eq("login")))
      .thenReturn(login);
    when(jsonObject.getString(eq("password")))
      .thenReturn(password);
    when(authService.verifyLogin(eq(login), anyString()))
      .thenReturn(true);
    when(databaseManager.getCollection(anyString()))
      .thenReturn(usersCollection);
    when(usersCollection.find(query))
      .thenReturn(documents);
    when(documents.first())
      .thenReturn(userDocument);
    when(userDocument.getString("id"))
      .thenReturn(userId);
    when(jwtAuth.generateToken(any(), any()))
      .thenReturn(refreshToken);
    when(databaseManager.getCollection(anyString()))
      .thenReturn(usersCollection);
    when(usersCollection.updateOne(query, update))
      .thenReturn(updateResult);
    when(updateResult.getModifiedCount())
      .thenReturn(0L);


    underTest.login(ctx);

    verify(response).setStatusCode(eq(400));
    verify(response).end(eq("Failed to update refreshToken"));
  }

  @Test
  void login_SuccessfulLogin() {
    final RoutingContext ctx = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonObject = mock(JsonObject.class);
    final String password = "password";
    final String login = "login";
    final MongoCollection<Document> usersCollection = mock(MongoCollection.class);
    final Document query = new Document("login", login);
    final FindIterable<Document> documents = mock(FindIterable.class);
    final Document userDocument = mock(Document.class);

    final String userId = "userId";
    final String refreshToken = "refreshToken";
    final Document update = new Document("$set", new Document("token", refreshToken));
    final UpdateResult updateResult = mock(UpdateResult.class);
    final JsonObject response = new JsonObject().put("token", refreshToken).put("message", "Witaj, " + login);
    final HttpServerResponse httpServerResponse = mock(HttpServerResponse.class);

    when(ctx.body())
      .thenReturn(requestBody);
    when(requestBody.asJsonObject())
      .thenReturn(jsonObject);
    when(jsonObject.getString(eq("login")))
      .thenReturn(login);
    when(jsonObject.getString(eq("password")))
      .thenReturn(password);
    when(authService.verifyLogin(eq(login), anyString()))
      .thenReturn(true);
    when(databaseManager.getCollection(anyString()))
      .thenReturn(usersCollection);
    when(usersCollection.find(query))
      .thenReturn(documents);
    when(documents.first())
      .thenReturn(userDocument);
    when(userDocument.getString("id"))
      .thenReturn(userId);
    when(jwtAuth.generateToken(any(), any()))
      .thenReturn(refreshToken);
    when(databaseManager.getCollection(anyString()))
      .thenReturn(usersCollection);
    when(usersCollection.updateOne(query, update))
      .thenReturn(updateResult);
    when(updateResult.getModifiedCount())
      .thenReturn(1L);
    when(ctx.response())
      .thenReturn(httpServerResponse);

    underTest.login(ctx);

    verify(httpServerResponse).putHeader(eq("content-type"), eq("application/json"));
    verify(httpServerResponse).setStatusCode(eq(200));
    verify(httpServerResponse).end(response.encode());
  }

}
