package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TaxaEntregaResponseDTO {
  private Long restauranteId;
  private String restauranteNome;
  private String cep;
  private BigDecimal taxaEntrega;
  private Double distanciaKm;
  private Integer tempoEstimadoMinutos;

  // Construtores
  public TaxaEntregaResponseDTO() {
  }

  public TaxaEntregaResponseDTO(Long restauranteId, String restauranteNome,
      String cep, BigDecimal taxaEntrega) {
    this.restauranteId = restauranteId;
    this.restauranteNome = restauranteNome;
    this.cep = cep;
    this.taxaEntrega = taxaEntrega;
  }
}
