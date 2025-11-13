package com.deliverytech.delivery_api.auth.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.deliverytech.delivery_api.pedido.model.Pedido;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;

import jakarta.persistence.Column;
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
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "usuario")
public class Usuario implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String senha;

  @Column(nullable = false)
  private String nome;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(nullable = false)
  private Boolean ativo = true;

  @Column(name = "data_criacao", nullable = false)
  private LocalDateTime dataCriacao = LocalDateTime.now();

  private String telefone;

  private String endereco;

  @OneToMany(mappedBy = "usuario")
  private List<Pedido> pedidos;

  // ✅ RELACIONAMENTO: Muitos usuários → Um restaurante
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurante_id")
  private Restaurante restaurante;

  public Usuario() {
  }

  public Usuario(String email, String senha, String nome, Role role) {
    this.email = email;
    this.senha = senha;
    this.nome = nome;
    this.role = role;
    this.ativo = true;
    this.dataCriacao = LocalDateTime.now();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return senha;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return ativo;
  }
}
