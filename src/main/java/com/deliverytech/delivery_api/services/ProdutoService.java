package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.dtos.ProdutoDTO;
import com.deliverytech.delivery_api.dtos.ProdutoFiltroDTO;
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
  Page<ProdutoResponseDTO> listarPorRestaurante(Long restauranteId, Pageable pageable);

  /**
   * Buscar produtos por categoria
   */
  Page<ProdutoResponseDTO> buscarPorCategoria(String categoria, Pageable pageable);

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
  Page<ProdutoResponseDTO> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax, Pageable pageable);

  /**
   * Buscar produtos por filtros diversos
   */
  Page<ProdutoResponseDTO> buscarComFiltros(ProdutoFiltroDTO filtro, Pageable pageable);

  /**
   * isOwner verifica se o usuário é o dono do produto
   * 
   * @param produtoId
   */
  boolean isOwner(Long produtoId);

  /**
   * Deletar produto
   */
  void deletarProduto(Long id);
}