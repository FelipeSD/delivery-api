package com.deliverytech.delivery_api.services;

import java.util.List;

import com.deliverytech.delivery_api.dtos.RestauranteDTO;
import com.deliverytech.delivery_api.dtos.RestauranteResponseDTO;
import com.deliverytech.delivery_api.dtos.TaxaEntregaResponseDTO;

public interface RestauranteService {

  /**
   * Cadastrar novo restaurante
   */
  RestauranteResponseDTO cadastrar(RestauranteDTO restauranteDTO);

  /**
   * Buscar restaurante por ID
   * 
   * @return RestauranteResponseDTO ou null se não encontrado
   */
  RestauranteResponseDTO buscarPorId(Long id);

  /**
   * Buscar restaurante por nome
   * @return Lista de RestauranteResponseDTO
   */
  List<RestauranteResponseDTO> buscarPorNome(String nome);

  /**
   * Buscar restaurante com produtos
   * @return RestauranteResponseDTO com lista de produtos ou null se não encontrado
   */
  RestauranteResponseDTO buscarComProdutos(Long id);

  /**
   * Listar restaurantes disponíveis (ativos)
   */
  List<RestauranteResponseDTO> listarDisponiveis();

  /**
   * Listar restaurantes por categoria
   */
  List<RestauranteResponseDTO> listarPorCategoria(String categoria);

  /**
   * Atualizar restaurante
   * 
   * @return RestauranteResponseDTO atualizado ou null se não encontrado
   */
  RestauranteResponseDTO atualizar(Long id, RestauranteDTO restauranteDTO);

  /**
   * Alterar status ativo/inativo do restaurante
   * @return RestauranteResponseDTO atualizado ou null se não encontrado
   */
  RestauranteResponseDTO alterarStatus(Long id, boolean ativo);

  /**
   * Calcular taxa de entrega para um CEP
   * 
   * @return Taxa de entrega 
   */
  TaxaEntregaResponseDTO calcularTaxaEntrega(Long id, String cep);
}