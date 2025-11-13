package com.deliverytech.delivery_api.common.exceptions;

public class OrderStatusException extends BusinessException {
  public OrderStatusException(Long orderId, String currentStatus, String attemptedStatus) {
    super(
        String.format("Order with ID %d cannot change status from %s to %s.", orderId, currentStatus, attemptedStatus),
        "order.status.invalid");
  }
}
