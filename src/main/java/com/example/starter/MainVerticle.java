package com.example.starter;

import com.example.starter.controller.DataController;
import com.example.starter.controller.ItemsController;
import com.example.starter.controller.LoginController;
import com.example.starter.controller.RegisterController;
import com.example.starter.database.MongoDatabaseManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  private MongoDatabaseManager databaseManager;

  public MainVerticle() {
  }

  @Override
  public void start() {
    databaseManager = new MongoDatabaseManager();

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    DataController dataController = new DataController(databaseManager);
    router.post("/add-data").handler(dataController::handleAddData);

    LoginController.register(router);
    RegisterController.register(router);
    ItemsController.register(router);

    server.requestHandler(router).listen(8888);
  }

  @Override
  public void stop() {
    if (databaseManager != null) {
      databaseManager.close();
    }
  }
}
