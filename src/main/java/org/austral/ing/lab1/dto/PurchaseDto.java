package org.austral.ing.lab1.dto;

public class PurchaseDto {
  private String title;
  private String quantity;
  private String unit_price;
  private String username;

  public PurchaseDto(String title, String quantity, String unit_price, String username) {
    this.title = title;
    this.quantity = quantity;
    this.unit_price = unit_price;
    this.username = username;
  }

  public String getTitle() {
    return title;
  }

  public int getQuantity() {
    return Integer.parseInt(quantity);
  }

  public int getPrice() {
    return Integer.parseInt(unit_price);
  }
  public String getUsername() {
    return username;
  }
}
