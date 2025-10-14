package com.deliverytech.delivery_api.dtos;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class RestauranteDTO {

  @NotBlank(message = "Nome é obrigatório")
  private String nome;

  @NotBlank(message = "CNPJ é obrigatório")
  @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
  private String cnpj;

  @NotBlank(message = "Email é obrigatório")
  @Email(message = "Email inválido")
  private String email;

  @NotBlank(message = "Telefone é obrigatório")
  private String telefone;

  @NotBlank(message = "Categoria é obrigatória")
  private String categoria;

  @NotNull(message = "Taxa de entrega é obrigatória")
  @DecimalMin(value = "0.0", message = "Taxa de entrega não pode ser negativa")
  private BigDecimal taxaEntrega;

  private Integer tempoEntregaMin;
  private Integer tempoEntregaMax;
  private String imagemUrl;
  private String endereco;
  private String cidade;
  private String estado;
  private String cep;
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
