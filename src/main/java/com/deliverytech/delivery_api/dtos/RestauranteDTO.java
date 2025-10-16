package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação/atualização de restaurante")
public class RestauranteDTO {

  @Schema(description = "Nome do restaurante", example = "Restaurante Bom Sabor", required = true)
  @NotBlank(message = "Nome é obrigatório")
  private String nome;

  @Schema(description = "CNPJ do restaurante", example = "12345678000199", required = true)
  @NotBlank(message = "CNPJ é obrigatório")
  @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
  private String cnpj;

  @Schema(description = "Email do restaurante", example = "email@test.com", required = true)
  @NotBlank(message = "Email é obrigatório")
  @Email(message = "Email inválido")
  private String email;

  @Schema(description = "Telefone do restaurante", example = "1699999-9999", required = true)
  @NotBlank(message = "Telefone é obrigatório")
  private String telefone;

  @Schema(description = "Categoria do restaurante", example = "Italiana", required = true)
  @NotBlank(message = "Categoria é obrigatória")
  private String categoria;

  @Schema(description = "Taxa de entrega do restaurante", example = "5.00", required = true)
  @NotNull(message = "Taxa de entrega é obrigatória")
  @DecimalMin(value = "0.0", message = "Taxa de entrega não pode ser negativa")
  private BigDecimal taxaEntrega;

  @Schema(description = "Tempo mínimo de entrega em minutos", example = "30", required = true)
  private Integer tempoEntregaMin;
  
  @Schema(description = "Tempo máximo de entrega em minutos", example = "45", required = true)
  private Integer tempoEntregaMax;

  @Schema(description = "URL da imagem do restaurante", example = "https://example.com/imagem.jpg")
  private String imagemUrl;

  @Schema(description = "Endereço do restaurante", example = "Rua das Flores, 123", required = true)
  private String endereco;

  @Schema(description = "Cidade onde o restaurante está localizado", example = "São Paulo", required = true)
  private String cidade;

  @Schema(description = "Estado onde o restaurante está localizado", example = "SP", required = true)
  private String estado;

  @Schema(description = "CEP do restaurante", example = "12345000", required = true)
  private String cep;

  @Schema(description = "Indica se o restaurante está ativo", example = "true", defaultValue = "true")
  private boolean ativo = true;

  // Getters e Setters
  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCnpj() {
    return cnpj;
  }

  public void setCnpj(String cnpj) {
    this.cnpj = cnpj;
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

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public BigDecimal getTaxaEntrega() {
    return taxaEntrega;
  }

  public void setTaxaEntrega(BigDecimal taxaEntrega) {
    this.taxaEntrega = taxaEntrega;
  }

  public Integer getTempoEntregaMin() {
    return tempoEntregaMin;
  }

  public void setTempoEntregaMin(Integer tempoEntregaMin) {
    this.tempoEntregaMin = tempoEntregaMin;
  }

  public Integer getTempoEntregaMax() {
    return tempoEntregaMax;
  }

  public void setTempoEntregaMax(Integer tempoEntregaMax) {
    this.tempoEntregaMax = tempoEntregaMax;
  }

  public String getImagemUrl() {
    return imagemUrl;
  }

  public void setImagemUrl(String imagemUrl) {
    this.imagemUrl = imagemUrl;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }

  public String getCidade() {
    return cidade;
  }

  public void setCidade(String cidade) {
    this.cidade = cidade;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }
}
