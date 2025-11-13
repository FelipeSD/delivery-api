package com.deliverytech.delivery_api.auth.dto;

import java.time.LocalDateTime;

import com.deliverytech.delivery_api.auth.model.Role;
import com.deliverytech.delivery_api.auth.model.Usuario;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
  private Long id;
  private String nome;
  private String email;
  private Role role;
  private Boolean ativo;
  private LocalDateTime dataCriacao;
  private Long restauranteId;

  // Construtores
  public UsuarioResponseDTO() {
  }

  public UsuarioResponseDTO(Usuario usuario) {
    this.id = usuario.getId();
    this.nome = usuario.getNome();
    this.email = usuario.getEmail();
    this.role = usuario.getRole();
    this.ativo = usuario.getAtivo();
    this.dataCriacao = usuario.getDataCriacao();
    this.restauranteId = usuario.getRestaurante() != null ? usuario.getRestaurante().getId() : null;
  }
}