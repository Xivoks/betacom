package com.example.starter.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class User {
  private UUID id;
  private String login;
  private String password;
  private List<Item> items;
}
