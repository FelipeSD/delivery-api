package com.deliverytech.delivery_api.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um item do pedido")
public class ItemPedidoDTO {

  @Schema(description = "ID do produto", example = "1", required = true)
  @NotNull(message = "Produto é obrigatório")
  @Positive(message = "Produto ID deve ser positivo")
  private Long produtoId;

  @Schema(description = "Quantidade do produto", example = "2", required = true)
  @NotNull(message = "Quantidade é obrigatória")
  @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
  @Max(value = 50, message = "Quantidade máxima é 50")
  private Integer quantidade;

  @Schema(description = "Observações adicionais sobre o item do pedido", example = "Sem cebola")
  @Size(max = 200, message = "Observações não podem exceder 200 caracteres")
  private String observacoes;

  // Getters e Setters
  public Long getProdutoId() {
    return produtoId;
  }

  public void setProdutoId(Long produtoId) {
    this.produtoId = produtoId;
  }

  public Integer getQuantidade() {
    return quantidade;
  }

  public void setQuantidade(Integer quantidade) {
    this.quantidade = quantidade;
  }

  public String getObservacoes() {
    return observacoes;
  }

  public void setObservacoes(String observacoes) {
    this.observacoes = observacoes;
  }
}
