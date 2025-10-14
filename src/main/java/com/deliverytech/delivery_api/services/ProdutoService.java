package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.util.List;

import com.deliverytech.delivery_api.dtos.ProdutoDTO;
import com.deliverytech.delivery_api.dtos.ProdutoResponseDTO;

public interface ProdutoService {

  /**
   * Cadastrar novo produto
   */
  ProdutoResponseDTO cadastrarProduto(ProdutoDTO produtoDTO);

  /**
   * Buscar produto por ID
   */
  ProdutoResponseDTO buscarProdutoPorId(Long id);

  /**
   * Listar produtos por restaurante
   */
  List<ProdutoResponseDTO> listarPorRestaurante(Long restauranteId);

  /**
   * Buscar produtos por categoria
   */
  List<ProdutoResponseDTO> buscarPorCategoria(String categoria);

  /**
   * Atualizar produto
   */
  ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO produtoDTO);

  /**
   * Alterar disponibilidade do produto
   */
  ProdutoResponseDTO alterarDisponibilidade(Long id, boolean disponivel);

  /**
   * Buscar produtos por faixa de preço
   */
  List<ProdutoResponseDTO> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax);

  /**
   * Buscar produtos por restaurante e categoria
   */
  List<ProdutoResponseDTO> buscarPorRestauranteECategoria(Long restauranteId, String categoria);
}