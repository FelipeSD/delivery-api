package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery_api.enums.StatusPedido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PedidoResponseDTO {
  private Long id;
  private String numeroPedido;
  private LocalDateTime dataPedido;

  @NotNull
  private String enderecoEntrega;

  @PositiveOrZero
  private BigDecimal subtotal;

  @PositiveOrZero
  private BigDecimal taxaEntrega;

  @PositiveOrZero
  private BigDecimal valorTotal;

  private String observacoes;

  private StatusPedido status;

  private UsuarioResumoDTO usuario;

  private RestauranteResumoDTO restaurante;

  private List<ItemPedidoDTO> itens;
}
