package com.deliverytech.delivery_api.pedido.model;

import java.math.BigDecimal;

import com.deliverytech.delivery_api.produto.model.Produto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "pedido", "produto" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PedidoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int quantidade;
  private BigDecimal precoUnitario;
  private BigDecimal subtotal;
  private String observacoes;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pedido_id", nullable = false)
  private Pedido pedido;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "produto_id")
  private Produto produto;

  // Calcular subtotal
  public void calcularSubtotal() {
    if (precoUnitario != null && quantidade > 0) {
      this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    } else {
      this.subtotal = BigDecimal.ZERO;
    }
  }
}