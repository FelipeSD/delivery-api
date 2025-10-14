package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProdutoDTO {

  @NotBlank(message = "Nome é obrigatório")
  private String nome;

  private String descricao;

  @NotNull(message = "Preço é obrigatório")
  @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
  private BigDecimal preco;

  @NotBlank(message = "Categoria é obrigatória")
  private String categoria;

  @NotNull(message = "ID do restaurante é obrigatório")
  private Long restauranteId;

  private String imagemUrl;

  private boolean disponivel = true;

  // Getters e Setters
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
