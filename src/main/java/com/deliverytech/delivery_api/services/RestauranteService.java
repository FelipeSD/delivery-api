package com.deliverytech.delivery_api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
  Page<RestauranteResponseDTO> buscarPorNome(String nome, Pageable pageable);

  /**
   * Buscar restaurante com produtos
   * @return RestauranteResponseDTO com lista de produtos ou null se não encontrado
   */
  RestauranteResponseDTO buscarComProdutos(Long id);

  /**
   * Listar restaurantes disponíveis (ativos)
   */
  Page<RestauranteResponseDTO> listarDisponiveis(Pageable pageable);

  /**
   * Listar restaurantes por categoria
   */
  Page<RestauranteResponseDTO> listarPorCategoria(String categoria, Pageable pageable);

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