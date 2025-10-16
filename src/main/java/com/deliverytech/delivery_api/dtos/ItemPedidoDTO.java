package com.deliverytech.delivery_api.dtos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um item do pedido")
public class ItemPedidoDTO {

  @Schema(description = "ID do produto", example = "1", required = true)
  @NotNull(message = "Produto é obrigatório")
  private Long produtoId;

  @Schema(description = "Quantidade do produto", example = "2", required = true)
  @NotNull(message = "Quantidade é obrigatória")
  @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
  @Max(value = 10, message = "Quantidade máxima é 10")
  private Integer quantidade;

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
}
