package com.example.starter;

import com.example.starter.controller.ItemsController;
import com.example.starter.controller.LoginController;
import com.example.starter.controller.RegisterController;
import com.example.starter.controller.TokenController;
import com.example.starter.database.MongoDatabaseManager;
import com.example.starter.handler.ErrorHandler;
import com.example.starter.jwtauth.JWTAuthConfig;
import com.example.starter.jwtauth.JWTAuthProvider;
import com.example.starter.service.AuthenticationService;
import com.example.starter.service.AuthenticationServiceImpl;
import com.example.starter.service.ItemsService;
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
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    JWTAuth jwtAuth = JWTAuthConfig.createJWTAuthProvider(vertx);
    JWTAuthProvider jwtAuthProvider = new JWTAuthProvider(jwtAuth);
    TokenController tokenController = new TokenController(jwtAuth, jwtAuthProvider, databaseManager.getDatabase());
    router.route("/secure/*").handler(tokenController::secureEndpoint);

    router.get("/generate-token").handler(tokenController::generateTokens);
    AuthenticationService authService = new AuthenticationServiceImpl(databaseManager);
    ItemsService itemsService = new ItemsService(databaseManager);

    LoginController loginController = new LoginController(jwtAuth, authService, databaseManager);
    loginController.register(router);

    RegisterController registerController = new RegisterController(databaseManager, jwtAuth);
    router.post("/register").handler(registerController::register);


    ItemsController itemsController = new ItemsController(jwtAuth,itemsService);
    itemsController.register(router);

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
