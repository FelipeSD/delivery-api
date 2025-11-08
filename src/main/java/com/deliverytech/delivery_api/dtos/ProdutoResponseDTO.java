package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
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
