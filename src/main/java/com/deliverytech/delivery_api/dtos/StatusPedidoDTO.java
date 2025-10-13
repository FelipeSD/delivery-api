package com.deliverytech.delivery_api.dtos;

import javax.validation.constraints.NotNull;

import com.deliverytech.delivery_api.enums.StatusPedido;

public class StatusPedidoDTO {
  @NotNull
  private StatusPedido status;

  public StatusPedidoDTO() {}

  public StatusPedidoDTO(StatusPedido status) {
    this.status = status;
  }

  public StatusPedido getStatus() {
    return status;
  }

  public void setStatus(StatusPedido status) {
    this.status = status;
  }
}
