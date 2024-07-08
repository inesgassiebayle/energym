package org.austral.ing.lab1.dto;

import java.time.*;

public class MembershipDto {
  String username;
  String expiration;

  public MembershipDto(String username, String expiration) {
    this.username = username;
    this.expiration = expiration;
  }

  public String getUsername() {
    return username;
  }

  public LocalDate getExpiration() {
    return LocalDate.parse(expiration);
  }
}
