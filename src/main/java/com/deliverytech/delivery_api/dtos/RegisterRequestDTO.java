package com.deliverytech.delivery_api.dtos;

import com.deliverytech.delivery_api.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para registro de novo usuário")
public class RegisterRequestDTO {

  @NotBlank(message = "Nome é obrigatório")
  @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
  private String nome;

  @NotBlank(message = "Email é obrigatório")
  @Email(message = "Email deve ter formato válido")
  private String email;

  @NotBlank(message = "Senha é obrigatória")
  @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
  private String senha;

  @NotNull(message = "Role é obrigatória")
  private Role role;

  private Long restauranteId;

  // Construtores
  public RegisterRequestDTO() {
  }

  public RegisterRequestDTO(String nome, String email, String senha, Role role) {
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.role = role;
  }

  // Ge ers e Se ers
  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

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

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Long getRestauranteId() {
    return restauranteId;
  }

  public void setRestauranteId(Long restauranteId) {
    this.restauranteId = restauranteId;
  }
}