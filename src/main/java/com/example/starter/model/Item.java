package com.example.starter.model;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Item {
  private UUID id;
  private UUID owner;
  private String name;
}
