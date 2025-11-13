package com.deliverytech.delivery_api.pedido.dto;

import com.deliverytech.delivery_api.pedido.model.PedidoStatus;

import jakarta.validation.constraints.NotNull;

public class PedidoStatusDTO {
  @NotNull
  private PedidoStatus status;

  public PedidoStatusDTO() {
  }

  public PedidoStatusDTO(PedidoStatus status) {
    this.status = status;
  }

  public PedidoStatus getStatus() {
    return status;
  }

  public void setStatus(PedidoStatus status) {
    this.status = status;
  }
}
