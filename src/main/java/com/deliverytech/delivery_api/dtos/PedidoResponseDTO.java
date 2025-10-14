package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.deliverytech.delivery_api.enums.StatusPedido;

public class PedidoResponseDTO {
  private Long id;
  private String numeroPedido;
  private LocalDateTime dataPedido;

  @NotNull
  private String enderecoEntrega;

  @PositiveOrZero
  private BigDecimal subtotal;

  @PositiveOrZero
  private BigDecimal taxaEntrega;

  @PositiveOrZero
  private BigDecimal valorTotal;

  private String observacoes;

  private StatusPedido status;

  private ClienteResponseDTO cliente;

  // Getters and setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNumeroPedido() {
    return numeroPedido;
  }

  public void setNumeroPedido(String numeroPedido) {
    this.numeroPedido = numeroPedido;
  }

  public LocalDateTime getDataPedido() {
    return dataPedido;
  }

  public void setDataPedido(LocalDateTime dataPedido) {
    this.dataPedido = dataPedido;
  }

  public String getEnderecoEntrega() {
    return enderecoEntrega;
  }

  public void setEnderecoEntrega(String enderecoEntrega) {
    this.enderecoEntrega = enderecoEntrega;
  }

  public BigDecimal getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(BigDecimal subtotal) {
    this.subtotal = subtotal;
  }

  public BigDecimal getTaxaEntrega() {
    return taxaEntrega;
  }

  public void setTaxaEntrega(BigDecimal taxaEntrega) {
    this.taxaEntrega = taxaEntrega;
  }

  public BigDecimal getValorTotal() {
    return valorTotal;
  }

  public void setValorTotal(BigDecimal valorTotal) {
    this.valorTotal = valorTotal;
  }

  public String getObservacoes() {
    return observacoes;
  }

  public void setObservacoes(String observacoes) {
    this.observacoes = observacoes;
  }

  public StatusPedido getStatus() {
    return status;
  }

  public void setStatus(StatusPedido status) {
    this.status = status;
  }

  public ClienteResponseDTO getCliente() {
    return cliente;
  }

  public void setCliente(ClienteResponseDTO cliente) {
    this.cliente = cliente;
  }
}
