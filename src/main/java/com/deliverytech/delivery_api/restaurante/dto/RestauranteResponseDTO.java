package com.deliverytech.delivery_api.restaurante.dto;

import java.math.BigDecimal;
import java.util.List;

import com.deliverytech.delivery_api.produto.dto.ProdutoResponseDTO;

import lombok.Data;

@Data
public class RestauranteResponseDTO {
  private Long id;
  private String nome;
  private String cnpj;
  private String email;
  private String telefone;
  private String categoria;
  private BigDecimal taxaEntrega;
  private Integer tempoEntregaMin;
  private Integer tempoEntregaMax;
  private String imagemUrl;
  private String endereco;
  private String cidade;
  private String estado;
  private String cep;
  private boolean ativo;
  private List<ProdutoResponseDTO> produtos;
}