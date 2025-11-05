package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProdutoFiltroDTO {
  private String nome;

  @Positive(message = "Preço deve ser positivo")
  private BigDecimal precoMin;

  @Positive(message = "Preço deve ser positivo")
  private BigDecimal precoMax;

  private String categoria;

  @Positive(message = "Restaurante ID deve ser positivo")
  private Long restauranteId;

  private Boolean disponivel;
}
