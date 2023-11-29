package com.example.starter.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class ErrorHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    int statusCode = ctx.statusCode();
    String errorMessage = "Wystąpił błąd podczas przetwarzania żądania.";

    if (statusCode == 401) {
      errorMessage = "Brak autoryzacji.";
    } else if (statusCode == 404) {
      errorMessage = "Strona nie znaleziona.";
    }

    ctx.response()
      .setStatusCode(statusCode)
      .end(errorMessage);
  }
}
