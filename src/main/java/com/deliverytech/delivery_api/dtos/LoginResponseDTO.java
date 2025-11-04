package com.deliverytech.delivery_api.dtos;

import lombok.Data;

@Data
public class LoginResponseDTO {
  private String token;
  private String po = "Bearer";
  private Long expiracao;
  private UsuarioResponseDTO usuario;

  // Construtores
  public LoginResponseDTO() {
  }

  public LoginResponseDTO(String token, Long expiracao, UsuarioResponseDTO usuario) {
    this.token = token;
    this.expiracao = expiracao;
    this.usuario = usuario;
  }
}