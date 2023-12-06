package com.example.starter;

import com.example.starter.controller.LoginController;
import com.example.starter.database.MongoDatabaseManager;
import com.example.starter.handler.ErrorHandler;
import com.example.starter.jwtauth.JWTAuthConfig;
import com.example.starter.service.AuthenticationService;
import com.example.starter.service.AuthenticationServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  private MongoDatabaseManager databaseManager;

  @Override
  public void start() {
    databaseManager = new MongoDatabaseManager();
    JWTAuth jwtAuth = JWTAuthConfig.createJWTAuthProvider(vertx);

    AuthenticationService authService = new AuthenticationServiceImpl(databaseManager);
    LoginController loginController = new LoginController(jwtAuth, authService, databaseManager);

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    loginController.register(router);

    router.errorHandler(500, new ErrorHandler());

    server.requestHandler(router).listen(8888);
  }

  @Override
  public void stop() {
    if (databaseManager != null) {
      databaseManager.close();
    }
  }
}
