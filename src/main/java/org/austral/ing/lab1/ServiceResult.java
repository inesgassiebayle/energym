package org.austral.ing.lab1;

public class ServiceResult {
  private boolean valid;
  private String message;

  public ServiceResult(boolean valid, String message) {
    this.valid = valid;
    this.message = message;
  }

  public boolean isValid() {
    return valid;
  }

  public String getMessage() {
    return message;
  }
}
