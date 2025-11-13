package com.deliverytech.delivery_api.pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.deliverytech.delivery_api.pedido.model.PedidoStatus;

import lombok.Data;

@Data
public class PedidoFiltroDTO {

  private PedidoStatus status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dataInicio;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dataFim;

  private BigDecimal valorMinimo;

  private BigDecimal valorMaximo;
}
