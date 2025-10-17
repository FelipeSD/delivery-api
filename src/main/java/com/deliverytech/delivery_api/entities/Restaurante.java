package com.deliverytech.delivery_api.entities;

import java.math.BigDecimal;
import java.util.List;

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
    private String nome;
    private String categoria;
    private String cnpj;
    private String email;
    private String cidade;
    private String estado;
    private String endereco;
    private String telefone;
    private BigDecimal taxaEntrega;
    private String cep;
    private Integer tempoEntregaMin;
    private Integer tempoEntregaMax;
    private Double avaliacao;
    private boolean ativo;

    @OneToMany(mappedBy = "restaurante")
    private List<Produto> produtos;

    @OneToMany(mappedBy = "restaurante")
    private List<Pedido> pedidos;
}