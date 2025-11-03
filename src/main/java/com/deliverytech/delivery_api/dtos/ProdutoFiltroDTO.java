package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProdutoFiltroDTO {
  private String nome;
  private BigDecimal precoMin;
  private BigDecimal precoMax;
  private String categoria;
  private Long restauranteId;
  private Boolean disponivel;
}
