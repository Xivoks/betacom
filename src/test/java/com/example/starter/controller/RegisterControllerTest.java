package com.example.starter.controller;

import com.example.starter.service.RegisterService;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

  @Mock
  private RegisterService registerService;

  @InjectMocks
  private RegisterController underTest;

  @Test
  void registration_RequestBodyNull() {
    final RoutingContext routingContext = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonObject = null;
    final HttpServerResponse response = mock(HttpServerResponse.class);

    when(routingContext.body()).thenReturn(requestBody);
    when(requestBody.asJsonObject()).thenReturn(jsonObject);
    when(routingContext.response()).thenReturn(response);

    when(response.setStatusCode(anyInt())).thenReturn(response);

    underTest.handleRegistration(routingContext);

    verify(response).setStatusCode(eq(400));
    verify(response).end(eq("Invalid request body format"));
  }

  @Test
  void registration_ValidRequestBody() {
    final RoutingContext routingContext = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonRequestBody = new JsonObject().put("login", "exampleLogin").put("password", "examplePassword");
    final HttpServerResponse response = mock(HttpServerResponse.class);
    final RegisterService mockRegisterService = mock(RegisterService.class);
    final RegisterController underTest = new RegisterController(mockRegisterService);

    when(routingContext.body()).thenReturn(requestBody);
    when(requestBody.asJsonObject()).thenReturn(jsonRequestBody);
    when(routingContext.response()).thenReturn(response);

    when(response.putHeader(anyString(), anyString())).thenReturn(response);
    when(response.setStatusCode(anyInt())).thenReturn(response);

    when(mockRegisterService.registerUser(anyString(), anyString())).thenReturn(true);



    underTest.handleRegistration(routingContext);

    verify(response).setStatusCode(eq(201));
    verify(response).putHeader(eq("content-type"), eq("application/json"));

    verify(mockRegisterService).registerUser(eq("exampleLogin"), eq("examplePassword"));

    ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
    verify(response).end(responseCaptor.capture());
    String responseJson = responseCaptor.getValue();
    assertTrue(responseJson.contains("Konto zostało utworzone"));
  }

  @Test
  void registration_UserAlreadyExists() {
    final RoutingContext routingContext = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonRequestBody = new JsonObject().put("login", "exampleLogin").put("password", "examplePassword");
    final HttpServerResponse response = mock(HttpServerResponse.class);
    final RegisterService mockRegisterService = mock(RegisterService.class);
    final RegisterController underTest = new RegisterController(mockRegisterService);

    when(routingContext.body()).thenReturn(requestBody);
    when(requestBody.asJsonObject()).thenReturn(jsonRequestBody);
    when(routingContext.response()).thenReturn(response);

    lenient().when(response.putHeader(anyString(), anyString())).thenReturn(response);
    lenient().when(response.setStatusCode(anyInt())).thenReturn(response);


    when(mockRegisterService.registerUser(anyString(), anyString())).thenReturn(false);


    underTest.handleRegistration(routingContext);

    verify(response).setStatusCode(eq(400));
    verify(response).end(eq("Użytkownik o podanym loginie już istnieje"));

    verify(mockRegisterService).registerUser(eq("exampleLogin"), eq("examplePassword"));
  }

  @Test
  void registration_UserRegisteredSuccessfully() {
    final RoutingContext routingContext = mock(RoutingContext.class);
    final RequestBody requestBody = mock(RequestBody.class);
    final JsonObject jsonRequestBody = new JsonObject().put("login", "exampleLogin").put("password", "examplePassword");
    final HttpServerResponse response = mock(HttpServerResponse.class);
    final RegisterService mockRegisterService = mock(RegisterService.class);
    final RegisterController underTest = new RegisterController(mockRegisterService);
    when(routingContext.body()).thenReturn(requestBody);
    when(requestBody.asJsonObject()).thenReturn(jsonRequestBody);
    when(routingContext.response()).thenReturn(response);

    when(response.putHeader(anyString(), anyString())).thenReturn(response);
    when(response.setStatusCode(anyInt())).thenReturn(response);


    when(mockRegisterService.registerUser(anyString(), anyString())).thenReturn(true);



    underTest.handleRegistration(routingContext);

    verify(response).setStatusCode(eq(201));
    verify(response).putHeader(eq("content-type"), eq("application/json"));

    verify(mockRegisterService).registerUser(eq("exampleLogin"), eq("examplePassword"));

    ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
    verify(response).end(responseCaptor.capture());
    String responseJson = responseCaptor.getValue();
    assertTrue(responseJson.contains("Konto zostało utworzone"));
  }
}
