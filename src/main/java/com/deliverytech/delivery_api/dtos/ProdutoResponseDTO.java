package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProdutoResponseDTO {
  private Long id;
  private String nome;
  private String descricao;
  private BigDecimal preco;
  private String categoria;
  private Long restauranteId;
  private String restauranteNome;
  private String imagemUrl;
  private boolean disponivel;
}
