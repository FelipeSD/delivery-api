package com.deliverytech.delivery_api.pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery_api.auth.dto.UsuarioResumoDTO;
import com.deliverytech.delivery_api.pedido.model.PedidoStatus;
import com.deliverytech.delivery_api.restaurante.dto.RestauranteResumoDTO;

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

  private PedidoStatus status;

  private UsuarioResumoDTO usuario;

  private RestauranteResumoDTO restaurante;

  private List<PedidoItemDTO> itens;
}
