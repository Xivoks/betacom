package com.example.starter;

import com.example.starter.controller.DataController;
import com.example.starter.controller.ItemsController;
import com.example.starter.controller.LoginController;
import com.example.starter.controller.RegisterController;
import com.example.starter.controller.TokenController;
import com.example.starter.database.MongoDatabaseManager;
import com.example.starter.handler.ErrorHandler;
import com.example.starter.jwtauth.JWTAuthConfig;
import com.example.starter.jwtauth.JWTAuthProvider;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  private MongoDatabaseManager databaseManager;

  @Override
  public void start() {
    databaseManager = new MongoDatabaseManager();

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    JWTAuth jwtAuth = JWTAuthConfig.createJWTAuthProvider(vertx);
    JWTAuthProvider jwtAuthProvider = new JWTAuthProvider(jwtAuth);

    DataController dataController = new DataController(databaseManager);
    TokenController tokenController = new TokenController(jwtAuth, jwtAuthProvider);

    router.route("/secure/*").handler(tokenController::secureEndpoint);

    router.get("/generate-token").handler(tokenController::generateToken);

    router.post("/secure/add-data").handler(dataController::handleAddData);

    LoginController.register(router);
    RegisterController.register(router);
    ItemsController.register(router);

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
