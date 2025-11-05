package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.deliverytech.delivery_api.enums.StatusPedido;

import lombok.Data;

@Data
public class PedidoFiltroDTO {

  private StatusPedido status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dataInicio;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dataFim;

  private BigDecimal valorMinimo;

  private BigDecimal valorMaximo;
}
