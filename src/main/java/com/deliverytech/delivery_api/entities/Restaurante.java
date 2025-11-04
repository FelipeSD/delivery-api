package com.deliverytech.delivery_api.entities;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Restaurante {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nome;

  private String categoria;

  @Column(nullable = false, unique = true, length = 18)
  private String cnpj;

  @Column(nullable = false, unique = true)
  private String email;

  private String cidade;
  private String estado;
  private String endereco;
  private String telefone;
  private String horarioFuncionamento;
  private BigDecimal taxaEntrega;
  private String cep;
  private Integer tempoEntregaMin;
  private Integer tempoEntregaMax;
  private Double avaliacao;
  private boolean ativo;

  @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Produto> produtos;

  @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Pedido> pedidos;

  @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Usuario> usuarios;
}
