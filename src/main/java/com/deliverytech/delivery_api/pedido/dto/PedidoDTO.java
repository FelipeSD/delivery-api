package com.deliverytech.delivery_api.pedido.dto;

import java.util.List;

import com.deliverytech.delivery_api.common.validations.ValidCEP;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Dados para criação de pedido")
public class PedidoDTO {

  @Schema(description = "ID do usuário que fez o pedido", example = "1", required = true)
  @NotNull(message = "Usuário é obrigatório")
  @Positive(message = "Usuário ID deve ser positivo")
  private Long usuarioId;

  @Schema(description = "ID do restaurante onde o pedido foi feito", example = "1", required = true)
  @NotNull(message = "Restaurante é obrigatório")
  @Positive(message = "Restaurante ID deve ser positivo")
  private Long restauranteId;

  @Schema(description = "Endereço de entrega do pedido", example = "Rua das Flores, 123, Centro, Cidade - Estado", required = true)
  @NotBlank(message = "Endereço de entrega é obrigatório")
  @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
  private String enderecoEntrega;

  @Schema(description = "CEP do endereço de entrega", example = "12345-678", required = true)
  @NotBlank(message = "CEP é obrigatório")
  @ValidCEP
  private String cep;

  @Schema(description = "Observações adicionais sobre o pedido", example = "Por favor, entregar após as 18h")
  @Size(max = 500, message = "Observações não podem exceder 500 caracteres")
  private String observacoes;

  @NotBlank(message = "Forma de pagamento é obrigatória")
  @Pattern(regexp = "^(DINHEIRO|CARTAO_CREDITO|CARTAO_DEBITO|PIX)$", message = "Forma de pagamento deve ser: DINHEIRO, CARTAO_CREDITO,  CARTAO_DEBITO ou PIX")
  private String formaPagamento;

  @Schema(description = "Lista de itens do pedido", required = true)
  @NotEmpty(message = "Pedido deve ter pelo menos um item")
  @Valid
  private List<PedidoItemDTO> itens;
}
