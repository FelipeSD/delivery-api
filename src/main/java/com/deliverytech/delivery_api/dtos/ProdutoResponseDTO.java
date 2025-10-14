package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

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

  // Getters e Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public BigDecimal getPreco() {
    return preco;
  }

  public void setPreco(BigDecimal preco) {
    this.preco = preco;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public Long getRestauranteId() {
    return restauranteId;
  }

  public void setRestauranteId(Long restauranteId) {
    this.restauranteId = restauranteId;
  }

  public String getRestauranteNome() {
    return restauranteNome;
  }

  public void setRestauranteNome(String restauranteNome) {
    this.restauranteNome = restauranteNome;
  }

  public String getImagemUrl() {
    return imagemUrl;
  }

  public void setImagemUrl(String imagemUrl) {
    this.imagemUrl = imagemUrl;
  }

  public boolean isDisponivel() {
    return disponivel;
  }

  public void setDisponivel(boolean disponivel) {
    this.disponivel = disponivel;
  }
}
