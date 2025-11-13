package com.deliverytech.delivery_api.common.exceptions;

public class EntityNotFoundException extends BusinessException {
  public EntityNotFoundException(String entity, Long id) {
    super(String.format("%s with ID %d not found.", entity, id), "entity.not.found");
  }
}
