package com.deliverytech.delivery_api.pedido.model;

public enum PedidoStatus {
  PENDENTE("Pendente"),
  CONFIRMADO("Confirmado"),
  PREPARANDO("Preparando"),
  SAIU_PARA_ENTREGA("Saiu para Entrega"),
  ENTREGUE("Entregue"),
  CANCELADO("Cancelado");

  private final String descricao;

  PedidoStatus(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }
}
