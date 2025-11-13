package com.deliverytech.delivery_api.common.exceptions;

public class InactiveEntityException extends BusinessException {
  public InactiveEntityException(String entity, Long id) {
    super(String.format("%s with ID %d is inactive.", entity, id), "entity.inactive");
  }
  
}
