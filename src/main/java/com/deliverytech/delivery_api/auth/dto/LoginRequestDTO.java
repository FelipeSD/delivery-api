package com.deliverytech.delivery_api.auth.dto;

import com.deliverytech.delivery_api.common.utils.LowercaseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

  @NotBlank(message = "Email é obrigatório")
  @Email(message = "Email deve ter formato válido")
  @JsonDeserialize(using = LowercaseDeserializer.class)
  private String email;

  @NotBlank(message = "Senha é obrigatória")
  private String senha;

  // Construtores
  public LoginRequestDTO() {
  }

  public LoginRequestDTO(String email, String senha) {
    this.email = email;
    this.senha = senha;
  }

  // Ge ers e Se ers
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSenha() {
    return senha;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }
}