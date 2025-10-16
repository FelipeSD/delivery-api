package com.deliverytech.delivery_api.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação/atualização de cliente")
public class ClienteDTO {

  @Schema(description = "Nome completo do cliente", example = "João da Silva", required = true)
  @NotBlank(message = "Nome é obrigatório")
  @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
  private String nome;

  @Schema(description = "Email do cliente", example = "email@test.com", required = true)
  @NotBlank(message = "Email é obrigatório")
  @Email(message = "Email deve ter formato válido")
  private String email;

  @Schema(description = "Telefone do cliente", example = "1699999-9999", required = true)
  @NotBlank(message = "Telefone é obrigatório")
  @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
  private String telefone;

  @Schema(description = "Endereço do cliente", example = "Rua das Flores, 123, Centro, Cidade - Estado", required = true)
  @NotBlank(message = "Endereço é obrigatório")
  @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
  private String endereco;

  // Getters e Setters
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

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }
}