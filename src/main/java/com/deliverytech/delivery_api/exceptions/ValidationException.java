package com.deliverytech.delivery_api.exceptions;

public class ValidationException extends BusinessException {
  public ValidationException(String message) {
    super(message, "validation.error");
  }
}
