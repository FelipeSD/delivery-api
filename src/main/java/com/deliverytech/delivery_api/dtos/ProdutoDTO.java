package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação/atualização de produto")
public class ProdutoDTO {

  @Schema(description = "Nome do produto", example = "Pizza Margherita", required = true)
  @NotBlank(message = "Nome é obrigatório")
  private String nome;

  @Schema(description = "Descrição detalhada do produto", example = "Pizza com molho de tomate, mussarela e manjericão")
  @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
  private String descricao;

  @Schema(description = "Preço do produto", example = "45.90", required = true)
  @NotNull(message = "Preço é obrigatório")
  @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
  @DecimalMax(value = "500.00", message = "Preço não pode exceder R$ 500,00")
  private BigDecimal preco;

  @Schema(description = "Categoria do produto", example = "Pizzas", required = true)
  @NotBlank(message = "Categoria é obrigatória")
  private String categoria;

  @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1", required = true)
  @NotNull(message = "ID do restaurante é obrigatório")
  @Positive(message = "Restaurante ID deve ser positivo")
  private Long restauranteId;

  @Schema(description = "URL da imagem do produto", example = "https://example.com/pizza.jpg")
  @Pattern(regexp = "^(https?://).*\\.(jpg|jpeg|png|gif)$", message = "URL da imagem deve ser válida e ter formato JPG, JPEG, PNG ou GIF")
  private String imagemUrl;

  @Schema(description = "Indica se o produto está disponível para venda", example = "true", defaultValue = "true")
  @AssertTrue(message = "Produto deve estar disponível por padrão")
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
