package com.example.starter;

import com.example.starter.controller.ItemsController;
import com.example.starter.controller.LoginController;
import com.example.starter.controller.RegisterController;
import com.mongodb.client.MongoClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  private MongoClient mongoClient;

  @Override
  public void start() {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    LoginController.register(router);
    RegisterController.register(router);
    ItemsController.register(router);

    server.requestHandler(router).listen(8888);
  }

  @Override
  public void stop() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }
}
