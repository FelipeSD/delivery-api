package com.deliverytech.delivery_api.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de pedido")
public class PedidoDTO {

  @Schema(description = "ID do cliente que fez o pedido", example = "1", required = true)
  @NotNull(message = "Cliente é obrigatório")
  private Long clienteId;

  @Schema(description = "ID do restaurante onde o pedido foi feito", example = "1", required = true)
  @NotNull(message = "Restaurante é obrigatório")
  private Long restauranteId;

  @Schema(description = "Endereço de entrega do pedido", example = "Rua das Flores, 123, Centro, Cidade - Estado", required = true)
  @NotBlank(message = "Endereço de entrega é obrigatório")
  @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
  private String enderecoEntrega;

  @Schema(description = "Lista de itens do pedido", required = true)
  @NotEmpty(message = "Pedido deve ter pelo menos um item")
  @Valid
  private List<ItemPedidoDTO> itens;

  // Getters e Setters
  public Long getClienteId() {
    return clienteId;
  }

  public void setClienteId(Long clienteId) {
    this.clienteId = clienteId;
  }

  public Long getRestauranteId() {
    return restauranteId;
  }

  public void setRestauranteId(Long restauranteId) {
    this.restauranteId = restauranteId;
  }

  public String getEnderecoEntrega() {
    return enderecoEntrega;
  }

  public void setEnderecoEntrega(String enderecoEntrega) {
    this.enderecoEntrega = enderecoEntrega;
  }

  public List<ItemPedidoDTO> getItens() {
    return itens;
  }

  public void setItens(List<ItemPedidoDTO> itens) {
    this.itens = itens;
  }
}
