package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

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

  // Getters e Setters
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

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public BigDecimal getTaxaEntrega() {
    return taxaEntrega;
  }

  public void setTaxaEntrega(BigDecimal taxaEntrega) {
    this.taxaEntrega = taxaEntrega;
  }

  public Double getDistanciaKm() {
    return distanciaKm;
  }

  public void setDistanciaKm(Double distanciaKm) {
    this.distanciaKm = distanciaKm;
  }

  public Integer getTempoEstimadoMinutos() {
    return tempoEstimadoMinutos;
  }

  public void setTempoEstimadoMinutos(Integer tempoEstimadoMinutos) {
    this.tempoEstimadoMinutos = tempoEstimadoMinutos;
  }
}
