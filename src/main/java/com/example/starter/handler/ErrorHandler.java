package com.example.starter.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class ErrorHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    if (ctx.response().ended()) {
      return;
    }

    Throwable failure = ctx.failure();
    if (failure != null) {
      ctx.response()
        .setStatusCode(500)
        .end("Internal Server Error");
    } else {
      ctx.response()
        .setStatusCode(404)
        .end("Not Found");
    }
  }
}
