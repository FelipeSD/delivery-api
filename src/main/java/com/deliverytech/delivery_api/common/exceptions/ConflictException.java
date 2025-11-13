package com.deliverytech.delivery_api.common.exceptions;

public class ConflictException extends BusinessException {
  public ConflictException(String entity, String field) {
    super(String.format("%s with the same %s already exists.", entity, field), "entity.conflict");
  }
}
