package com.deliverytech.delivery_api.pedido.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString(exclude = { "itens", "usuario", "restaurante" }) // ✅ Evita loop
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pedido {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String numeroPedido;
  private LocalDateTime dataPedido;
  private String enderecoEntrega;
  private BigDecimal subtotal;
  private BigDecimal taxaEntrega;
  private BigDecimal valorTotal;
  private String cep;
  private String observacoes;
  private String formaPagamento;

  @Enumerated(EnumType.STRING)
  private PedidoStatus status;

  @ManyToOne
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @ManyToOne
  @JoinColumn(name = "restaurante_id")
  private Restaurante restaurante;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<PedidoItem> itens;

  public Pedido() {
    this.dataPedido = LocalDateTime.now();
  }

  public void adicionarItem(PedidoItem item) {
    if (this.itens == null) {
      this.itens = new ArrayList<>();
    }
    this.itens.add(item);
    item.setPedido(this);
  }

  public void removerItem(PedidoItem item) {
    this.itens.remove(item);
    item.setPedido(null);
  }

  public void confirmar() {
    if (this.status != PedidoStatus.PENDENTE) {
      throw new IllegalStateException("Pedido não está em estado PENDENTE");
    }
    this.status = PedidoStatus.CONFIRMADO;
  }
}